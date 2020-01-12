package br.com.jabolina.sharder.core.cluster;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

/**
 * Basic cluster element type, everyone is a member
 *
 * @author jab
 * @date 1/11/20
 */
@SuppressWarnings("UnstableApiUsage")
public interface Member {

  /**
   * Name of the member
   *
   * @return member name
   */
  String getName();

  /**
   * Get hashed member name
   *
   * @return hashed member name
   */
  default byte[] hashName() {
    return Hashing.murmur3_32()
        .hashBytes(getName().getBytes(StandardCharsets.UTF_8))
        .asBytes();
  }

  /**
   * Used to insert one member into another
   *
   * @param member: member that wants to be introduce
   */
  default void ehlo(Member member) {
  }
}
