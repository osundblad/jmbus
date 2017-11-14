package org.openmuc.jmbus.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FiveBitStringTest {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random random = new Random();

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @Test
    public void decodeManufacturerId() {
        assertEquals( "ABB", FiveBitString.decodeManufacturerId(new byte[]{0b01000010, 0b00000100}, 0));
    }

    @Test
    public void encodeManufacturerIdString() {
        assertTrue(Arrays.equals(new byte[]{0b01000010, 0b00000100}, FiveBitString.encodeManufacturerIdString("ABB")));
        assertTrue(Arrays.equals(new byte[]{(byte) 0b11100110, 0b00011110}, FiveBitString.encodeManufacturerIdString("GWF")));
        assertTrue(Arrays.equals(new byte[]{(byte) 0b00110110, 0b00011100}, FiveBitString.encodeManufacturerIdString("GAV")));
    }

    @Test
    public void roundtrip() {
        for (int i = 0; i < 1000; i++) {
            final String manufacturer = randomManufacturer();
            assertEquals(manufacturer, FiveBitString.decodeManufacturerId(FiveBitString.encodeManufacturerIdString(manufacturer)));
        }
    }

    @Test
    public void encodeManufacturerIdString_wrongLength_shouldThrow() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("ManufacturerId should be 3 characters (or null/empty string)!");
        FiveBitString.encodeManufacturerIdString("AB");
    }

    private String randomManufacturer() {
        return "" + randomChar() + randomChar() + randomChar();
    }

    private char randomChar() {
        return CHARS.charAt(random.nextInt(CHARS.length()));
    }

}