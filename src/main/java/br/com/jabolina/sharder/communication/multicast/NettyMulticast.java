package br.com.jabolina.sharder.communication.multicast;

import br.com.jabolina.sharder.communication.Address;
import br.com.jabolina.sharder.concurrent.ConcurrentNamingFactory;
import br.com.jabolina.sharder.exception.SharderRuntimeException;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.Enumeration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Multicast service for communication, the communication will use UDP packets.
 * </p>
 * Since this implementation is using UDP there is no delivery nor order guarantee.
 *
 * @author jabolina
 * @date 1/12/20
 */
public class NettyMulticast implements MulticastComponent {
  private static final Logger LOGGER = LoggerFactory.getLogger(NettyMulticast.class);
  private static final String MULTICAST_THREAD_NAME = "mcast-nio-%d";
  private final InetSocketAddress groupAddress;
  private final AtomicBoolean enabled;
  private final NetworkInterface networkInterface;
  private final EventLoopGroup group;
  private InetAddress localAddress;
  private NioDatagramChannel serverChannel;
  private DatagramChannel clientChannel;

  private NettyMulticast(Address localAddr, Address groupAddr) {
    this.groupAddress = new InetSocketAddress(groupAddr.getHost(), groupAddr.getPort());
    this.enabled = new AtomicBoolean(false);
    this.group = new NioEventLoopGroup(0, ConcurrentNamingFactory.name(MULTICAST_THREAD_NAME, LOGGER));
    this.networkInterface = networkInterface(localAddr);
    this.localAddress = localAddress();
    LOGGER.debug("Building netty for [{}] and group [{}]", localAddr, groupAddr);
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public void multicast(String subject, byte[] message) {
    if (enabled.get()) {
      // TODO: create message object, serialize
      ByteBuf buf = serverChannel.alloc().buffer(4 + message.length);
      buf.writeInt(message.length).writeBytes(message);
      serverChannel.writeAndFlush(new DatagramPacket(buf, groupAddress));
    }
  }

  @Override
  public void subscribe(String subject, Consumer<byte[]> listener) {

  }

  @Override
  public void unsubscribe(String subject, Consumer<byte[]> listener) {

  }

  @Override
  public CompletableFuture<MulticastComponent> start() {
    if (!enabled.get()) {
      return server()
          .thenCompose(ignore -> client())
          .thenRun(() -> enabled.set(true))
          .thenApply(v -> this);
    }

    return CompletableFuture.completedFuture(this);
  }

  @Override
  public CompletableFuture<Void> stop() {
    if (!enabled.get()) {
      return CompletableFuture.completedFuture(null);
    }
    CompletableFuture<Void> future = new CompletableFuture<>();
    clientChannel.leaveGroup(groupAddress, networkInterface).addListener(ignore -> {
      enabled.set(false);
      group.shutdownGracefully();
      future.complete(null);
    });

    return future;
  }

  @Override
  public boolean isRunning() {
    return enabled.get();
  }

  /**
   * Builder for the multicast netty implementation
   */
  public static class Builder extends Multicast.Builder<NettyMulticast, Builder> {
    @Override
    public NettyMulticast build() {
      return new NettyMulticast(localAddr, groupAddr);
    }
  }

  private Bootstrap bootstrap() {
    return new Bootstrap()
        .group(group)
        .channelFactory(() -> new NioDatagramChannel(InternetProtocolFamily.IPv4))
        .option(ChannelOption.IP_MULTICAST_IF, networkInterface)
        .option(ChannelOption.SO_REUSEADDR, true);
  }

  private CompletableFuture<Void> server() {
    CompletableFuture<Void> future = new CompletableFuture<>();
    bootstrap()
        .handler(new SimpleChannelInboundHandler<DatagramPacket>() {
          @Override
          protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) {
            // do nothing
          }
        })
        .bind(groupAddress.getPort()).addListener((ChannelFutureListener) cfl -> {
          if (cfl.isSuccess()) {
            serverChannel = (NioDatagramChannel) cfl.channel();
            future.complete(null);
          } else {
            future.completeExceptionally(cfl.cause());
          }
        });

    return future;
  }

  private CompletableFuture<Void> client() {
    CompletableFuture<Void> future = new CompletableFuture<>();
    bootstrap()
        .localAddress(groupAddress.getPort())
        .handler(new ChannelInitializer<NioDatagramChannel>() {
          @Override
          protected void initChannel(NioDatagramChannel nioDatagramChannel) {
            nioDatagramChannel.pipeline().addLast(new MulticastHandler());
          }
        }).bind()
        .addListener((ChannelFutureListener) cfl -> {
          if (cfl.isSuccess()) {
            clientChannel = (DatagramChannel) cfl.channel();
            clientChannel.joinGroup(groupAddress, networkInterface).addListener(gf -> {
              if (gf.isSuccess()) {
                LOGGER.info("[{}] joined mcast group [{}]", localAddress.getHostName(), groupAddress.getHostName());
                future.complete(null);
              } else {
                LOGGER.error("[{}] failed joining mcast group [{}]", localAddress.getHostName(), groupAddress.getHostName());
                future.completeExceptionally(gf.cause());
              }
            });
          } else {
            future.completeExceptionally(cfl.cause());
          }
        });

    return future;
  }

  private InetAddress localAddress() {
    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
    while (addresses.hasMoreElements()) {
      InetAddress a = addresses.nextElement();
      if (a instanceof Inet4Address) {
        return a;
      }
    }

    throw new SharderRuntimeException("Could not find local inet address");
  }

  private NetworkInterface networkInterface(Address localAddr) {
    try {
      return NetworkInterface.getByInetAddress(localAddr.address());
    } catch (SocketException e) {
      throw new SharderRuntimeException(e);
    }
  }

  class MulticastHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
      // TODO: deserialize, notify listeners
      LOGGER.info("received data from mcast");
    }
  }
}
