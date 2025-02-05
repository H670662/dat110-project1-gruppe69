package no.hvl.dat110.messaging;

import java.util.Arrays;

import no.hvl.dat110.TODO;

public class MessageUtils {

    public static final int SEGMENTSIZE = 128;

    public static int MESSAGINGPORT = 8080;
    public static String MESSAGINGHOST = "localhost";

    public static byte[] encapsulate(Message message) {
        // TODO - START
        byte[] data = message.getData();
        byte[] segment = new byte[128];


        segment[0] = (byte) data.length;

        System.arraycopy(data, 0, segment, 1, data.length);
        return segment;
        // TODO - END
    }

    public static Message decapsulate(byte[] segment) {


        // TODO - START
        // decapsulate segment and put received payload data into a message

        if(segment == null) {
            throw new IllegalArgumentException("Segment is null");
        }

        if(segment.length == 0) {
            throw new IllegalArgumentException("Segment is empty");
        }
        // & er en Bitwise AND e.g 0101 AND 1011 er 0001
        // & 0xFF passer på at tall over 127 fortsatt er positive tall
        // todo, fjerne 0xFF og se om det fortsatt funker siden alt er under 127
        int length = segment[0] & 0xFF;

        if(length > 127) {
            throw new IllegalArgumentException("Segment is too long");
        }

        if(segment.length < 1 + length) {
            throw new IllegalArgumentException("Segment is too short");
        }

        byte[] data = new byte[length];
        System.arraycopy(segment, 1, data, 0, length);

        return new Message(data);

        // TODO - END
    }

}
