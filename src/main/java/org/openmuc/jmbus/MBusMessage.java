/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openmuc.jmbus;

/**
 * 
 * Represents a wired M-Bus link layer message according to EN 13757-2. The messages are in format class FT 1.2
 * according to IEC 60870-5-2.
 * 
 * If the M-Bus message is of frame type Long Frame it contains user data and it contains the following fields:
 * <ul>
 * <li>Length (1 byte) -</li>
 * <li>Control field (1 byte) -</li>
 * <li>Address field (1 byte) -</li>
 * <li>CI field (1 byte) -</li>
 * <li>The APDU (Variable Data Response) -</li>
 * </ul>
 */
public class MBusMessage {

    public static final int RSP_UD_HEADER_LENGTH = 6;
    // 261 is the maximum size of a long frame
    public static final int MAX_MESSAGE_SIZE = 261;

    public static final int TYPE_RSP_UD = 0x68;
    public static final int TYPE_SINGLE_CHARACTER = 0xE5;

    public enum MessageType {
        // the other message types (e.g. SND_NKE, REQ_UD2) cannot be sent from slave to master and are therefore
        // omitted.
        SINGLE_CHARACTER(TYPE_SINGLE_CHARACTER),
        RSP_UD(TYPE_RSP_UD);

        private static final MessageType[] VALUES = values();
        private final int value;

        MessageType(int value) {
            this.value = value;
        }

        private static MessageType messageTypeFor(byte value) throws DecodingException {
            int vAsint = value & 0xff;
            for (MessageType messageType : VALUES) {
                if (vAsint == messageType.value) {
                    return messageType;
                }
            }
            throw new DecodingException(String.format("Unexpected first frame byte: 0x%02X.", value));
        }

    }

    private final MessageType messageType;
    private final int addressField;
    private final VariableDataStructure variableDataStructure;

    private MBusMessage(MessageType messageType, int addressField, VariableDataStructure variableDataStructure) {
        this.messageType = messageType;
        this.addressField = addressField;
        this.variableDataStructure = variableDataStructure;
    }

    public static MBusMessage decode(byte[] buffer, int length) throws DecodingException {
        final MessageType messageType = MessageType.messageTypeFor(buffer[0]);
        final int addressField;
        final VariableDataStructure variableDataStructure;

        switch (messageType) {
        case SINGLE_CHARACTER:
            addressField = 0;
            variableDataStructure = null;
            break;
        case RSP_UD:
            int messageLength = getLongFrameMessageLength(buffer, length);
            checkLongFrameFields(buffer);
            addressField = buffer[5] & 0xff;
            variableDataStructure = new VariableDataStructure(buffer, RSP_UD_HEADER_LENGTH, messageLength, null, null);
            break;
        default:
            // should not occur.
            throw new RuntimeException("Case not supported " + messageType);
        }

        return new MBusMessage(messageType, addressField, variableDataStructure);
    }

    private static void checkLongFrameFields(byte[] buffer) throws DecodingException {
        if (buffer[1] != buffer[2]) {
            throw new DecodingException("Length fields are not identical in long frame!");
        }

        if (buffer[3] != MessageType.RSP_UD.value) {
            throw new DecodingException("Fourth byte of long frame was not 0x68.");
        }

        int controlField = buffer[4] & 0xff;

        if ((controlField & 0xcf) != 0x08) {
            throw new DecodingException(String.format("Unexpected control field value: 0x%02X.", controlField));
        }
    }

    private static int getLongFrameMessageLength(byte[] buffer, int length) throws DecodingException {
        int messageLength = buffer[1] & 0xff;

        if (messageLength != length - RSP_UD_HEADER_LENGTH) {
            throw new DecodingException("Wrong length field in frame header does not match the buffer length. Length field: "
                    + messageLength + ", buffer length: " + length + " !");
        }
        return messageLength;
    }

    public int getAddressField() {
        return addressField;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public VariableDataStructure getVariableDataResponse() {
        return variableDataStructure;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("message type: ")
                .append(messageType)
                .append("\naddress field: ")
                .append(addressField & 0xff)
                .append("\nVariable Data Structure:\n")
                .append(variableDataStructure)
                .toString();
    }

}
