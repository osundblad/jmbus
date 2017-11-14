package org.openmuc.jmbus;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class SecondaryAddressTest {

    @Test
    public void from_deviceIdManufacturerIdVersionDeviceType() {
        final Bcd deviceId = Bcd.from(12_345_678);
        final DeviceType deviceType = DeviceType.ELECTRICITY_METER;
        final int version = 32;
        final String manufacturerId = "ABB";
        final SecondaryAddress address = SecondaryAddress.from(deviceId, manufacturerId, version, deviceType);


        assertEquals(deviceId, address.getDeviceId());
        assertEquals(deviceType, address.getDeviceType());
        assertEquals(version, address.getVersion());
        assertEquals(manufacturerId, address.getManufacturerId());
    }

    @Test
    public void from_deviceIdManufacturerIdVersionDeviceType_realData() {
        final Bcd deviceId = Bcd.from(90_004_130);
        final DeviceType deviceType = DeviceType.ELECTRICITY_METER;
        final int version = 60;
        final String manufacturerId = "GAV";
        final SecondaryAddress address = SecondaryAddress.from(deviceId, manufacturerId, version, deviceType);

        assertEquals(deviceId, address.getDeviceId());
        assertEquals(deviceType, address.getDeviceType());
        assertEquals(version, address.getVersion());
        assertEquals(manufacturerId, address.getManufacturerId());
        System.out.println("address = " + address);
    }

    @Test
    public void getVersion_shouldAlwaysBePositive() {
        final byte[] buffer = {81, 37, 115, 20,  -26, 29,  -60, 6,};
        final SecondaryAddress address = SecondaryAddress.newFromLongHeader(buffer, 0);

        assertEquals(Byte.toUnsignedInt((byte) -60), address.getVersion());
        assertEquals(196, address.getVersion());
    }

    @Test
    public void from_deviceIdManufacturerIdVersionDeviceType_noManufacturerId() {
        final Bcd deviceId = Bcd.from(4711);
        final DeviceType deviceType = DeviceType.WATER_METER;
        final int version = 17;
        final SecondaryAddress address = SecondaryAddress.from(deviceId, "", version, deviceType);


        assertEquals(deviceId, address.getDeviceId());
        assertEquals(deviceType, address.getDeviceType());
        assertEquals(version, address.getVersion());
        assertEquals("@@@", address.getManufacturerId());
    }

    @Test
    public void getFromLongHeader_withOffset() {
        final byte[] buffer = {
                104, 27, 27, 104, 8, 0, 114, /* offset */   0x51, 0x25, 0x73, 0x14, (byte) 0xe6, 0x1e, 0x3c, 0x6,  /* garbage */   48, 0, 0, 0, 12, 120, 81, 37, 115, 20, 12, 19, 86, 0, 0, 0, -29, 22};
        final SecondaryAddress address = SecondaryAddress.newFromLongHeader(buffer, 7);

        assertEquals(Bcd.from(14732551), address.getDeviceId());
        assertEquals(DeviceType.WARM_WATER_METER, address.getDeviceType());
        assertEquals(60, address.getVersion());
        assertEquals("GWF", address.getManufacturerId());

        assertEquals(ByteBuffer.wrap(buffer, 7, 8), ByteBuffer.wrap(address.asByteArray()));
    }

    @Test
    public void realData() {
        final byte[] buffer = {
                0x72, 0x44, 0x76, 0x27, 0x16, (byte)0xe6, 0x1e, 0x3c,
                0x06, 0x45, 0x00, 0x00, 0x00, 0x0c, 0x78, 0x44,
                0x76, 0x27, 0x16, 0x0c, 0x13, (byte)0x83, 0x00, 0x00,
                0x00, 0x1a, 0x16
        };

        final SecondaryAddress fromLongHeader = SecondaryAddress.newFromLongHeader(buffer, 1);

        assertEquals("GWF", fromLongHeader.getManufacturerId());
        assertEquals(60, fromLongHeader.getVersion());
        assertEquals(DeviceType.WARM_WATER_METER, fromLongHeader.getDeviceType());
        assertEquals(16277644, fromLongHeader.getDeviceId().intValue());
    }
}