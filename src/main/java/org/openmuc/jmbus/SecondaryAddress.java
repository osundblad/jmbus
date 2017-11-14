/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openmuc.jmbus;

import org.openmuc.jmbus.util.FiveBitString;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * This class represents a secondary address. Use the static initalizer to initialize the
 */
public class SecondaryAddress implements Comparable<SecondaryAddress> {

    private static final int SECONDARY_ADDRESS_LENGTH = 8;

    private final String manufacturerId;
    private final Bcd deviceId;
    private final int version;
    private final DeviceType deviceType;
    private final byte[] bytes;
    private final int hashCode;
    private final boolean isLongHeader;

    public static SecondaryAddress from(final int deviceId, final String manufacturerId, final int version, final DeviceType deviceType) {
        return from(Bcd.from(deviceId), manufacturerId, version, deviceType);
    }

    public static SecondaryAddress from(final Bcd bcd, final String manufacturerId, final int version, final DeviceType deviceType) {
        final byte[] idString = FiveBitString.encodeManufacturerIdString(manufacturerId);
        final byte[] array = ByteBuffer.allocate(14)
                .put(bcd.getBytes())
                .put(idString)
                .put((byte) version)
                .put((byte) deviceType.getId()).array();
        return new SecondaryAddress(array, 0 , true);
    }

    /**
     * Instantiate a new secondary address within a long header.
     * 
     * @param buffer
     *            the byte buffer.
     * @param offset
     *            the offset.
     * @return a new secondary address.
     */
    public static SecondaryAddress newFromLongHeader(byte[] buffer, int offset) {
        return new SecondaryAddress(buffer, offset, true);
    }

    /**
     * Instantiate a new secondary address within a wireless M-Bus link layer header.
     * 
     * @param buffer
     *            the byte buffer.
     * @param offset
     *            the offset.
     * @return a new secondary address.
     */
    public static SecondaryAddress newFromWMBusLlHeader(byte[] buffer, int offset) {
        return new SecondaryAddress(buffer, offset, false);
    }

    /**
     * Instantiate a new secondary address for a manufacturer ID.
     * 
     * @param idNumber
     *            ID number.
     * @param manufactureId
     *            manufacturer ID.
     * @param version
     *            the version.
     * @param media
     *            the media.
     * @return a new secondary address.
     * @throws NumberFormatException
     *             if the idNumber is not long enough.
     */
    public static SecondaryAddress newFromManufactureId(byte[] idNumber, String manufactureId, byte version, byte media)
            throws NumberFormatException {
        if (idNumber.length != SECONDARY_ADDRESS_LENGTH) {
            throw new NumberFormatException("Wrong length of ID. Length must be 8 byte.");
        }

        byte[] mfId = encodeManufacturerId(manufactureId);
        byte[] buffer = ByteBuffer.allocate(idNumber.length + mfId.length + 1 + 1)
                .put(idNumber)
                .put(mfId)
                .put(version)
                .put(media)
                .array();
        return new SecondaryAddress(buffer, 0, true);
    }

    /**
     * The {@link SecondaryAddress} as byte array.
     * 
     * @return the byte array (octet string) representation.
     */
    public byte[] asByteArray() {
        return bytes;
    }

    /**
     * Get the manufacturer ID.
     * 
     * @return the ID.
     */
    public String getManufacturerId() {
        return manufacturerId;
    }

    /**
     * Returns the device ID. This is secondary address of the device.
     * 
     * @return the device ID
     */
    public Bcd getDeviceId() {
        return deviceId;
    }

    /**
     * Returns the device type (e.g. gas, water etc.)
     * 
     * @return the device type
     */
    public DeviceType getDeviceType() {
        return deviceType;
    }

    /**
     * Get the version.
     * 
     * @return the version.
     */
    public int getVersion() {
        return version;
    }

    public boolean isLongHeader() {
        return isLongHeader;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("manufacturer ID: ")
                .append(manufacturerId)
                .append(", device ID: ")
                .append(deviceId)
                .append(", device version: ")
                .append(version)
                .append(", device type: ")
                .append(deviceType)
                .append(", as bytes: ")
                .append(DatatypeConverter.printHexBinary(bytes))
                .toString();
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SecondaryAddress)) {
            return false;
        }

        SecondaryAddress other = (SecondaryAddress) obj;

        return Arrays.equals(this.bytes, other.bytes);
    }

    @Override
    public int compareTo(SecondaryAddress sa) {
        return Integer.compare(hashCode(), sa.hashCode());
    }

    private SecondaryAddress(byte[] buffer, int offset, boolean longHeader) {
        this.bytes = Arrays.copyOfRange(buffer, offset, offset + SECONDARY_ADDRESS_LENGTH);

        this.hashCode = Arrays.hashCode(this.bytes);
        this.isLongHeader = longHeader;

        if (longHeader) {
            deviceId = Bcd.from(bytes, 0);
            manufacturerId = FiveBitString.decodeManufacturerId(bytes, 4);
        } else {
            manufacturerId = FiveBitString.decodeManufacturerId(bytes, 0);
            deviceId = Bcd.from(bytes, 2);
        }
        version = Byte.toUnsignedInt(bytes[6]);
        deviceType = DeviceType.getInstance(bytes[7]);
    }

    private static byte[] encodeManufacturerId(String manufactureId) {
        if (manufactureId.length() != 3) {
            return new byte[] { 0, 0 };
        }

        manufactureId = manufactureId.toUpperCase();

        char[] manufactureIdArray = manufactureId.toCharArray();
        int manufacturerIdAsInt = (manufactureIdArray[0] - 64) * 32 * 32;
        manufacturerIdAsInt += (manufactureIdArray[1] - 64) * 32;
        manufacturerIdAsInt += (manufactureIdArray[1] - 64);

        return ByteBuffer.allocate(4).putInt(manufacturerIdAsInt).array();
    }

}
