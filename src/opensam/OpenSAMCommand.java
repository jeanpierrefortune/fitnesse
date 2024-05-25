/* **************************************************************************************
 * Copyright (c) 2024 Calypso Networks Association https://calypsonet.org/
 *
 * See the NOTICE file(s) distributed with this work for additional information
 * regarding copyright ownership.
 *
 * This software and the accompanying materials are private and confidential.
 * Unauthorized copying, distribution, modification, public display, or public
 * performance of this material is strictly prohibited.
 * This software is provided "AS IS" and any express or implied warranties, including,
 * but not limited to, the implied warranties of merchantability and fitness for a
 * particular purpose are disclaimed.
 *
 * All rights reserved.
 ************************************************************************************** */
package opensam;

/**
 * The OpenSAMCommand enum represents commands supported by OpenSAM.
 *
 * @since 0.1.0
 */
enum OpenSAMCommand {

  // Name, size, cla, ins, p1, p2, lc, le
  GET_CHALLENGE("Get Challenge", 5, (byte) 0, (byte) 0x84, (byte) 0, (byte) 0, (byte) 0, (byte) 4);

  private final String name;
  private final int apduSize;
  private final byte cla;
  private final byte ins;
  private final byte defaultP1;
  private final byte defaultP2;
  private final byte defaultLc;
  private final byte defaultLe;

  OpenSAMCommand(
      String name,
      int apduSize,
      byte cla,
      byte ins,
      byte defaultP1,
      byte defaultP2,
      byte defaultLc,
      byte defaultLe) {
    this.name = name;
    this.apduSize = apduSize;
    this.cla = cla;
    this.ins = ins;
    this.defaultP1 = defaultP1;
    this.defaultP2 = defaultP2;
    this.defaultLc = defaultLc;
    this.defaultLe = defaultLe;
  }

  public String getName() {
    return name;
  }

  public int getApduSize() {
    return apduSize;
  }

  public byte getCla() {
    return cla;
  }

  public byte getIns() {
    return ins;
  }

  public byte getDefaultP1() {
    return defaultP1;
  }

  public byte getDefaultP2() {
    return defaultP2;
  }

  public byte getDefaultLc() {
    return defaultLc;
  }

  public byte getDefaultLe() {
    return defaultLe;
  }
}
