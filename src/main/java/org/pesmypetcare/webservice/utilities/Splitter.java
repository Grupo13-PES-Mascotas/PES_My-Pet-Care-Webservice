package org.pesmypetcare.webservice.utilities;

import com.google.cloud.firestore.Blob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Santiago Del Rey
 */
public class Splitter {
    private static final double MAX_CHUNK_SIZE = Math.pow(2, 20);

    private Splitter() { }

    public static List<Blob> splitImage(byte[] image) {
        int length = image.length;
        List<Blob> imageChunks = new ArrayList<>();
        int chunk = 0;
        if (length > MAX_CHUNK_SIZE) {
            for (int i = 0; i < length; i += MAX_CHUNK_SIZE) {
                byte[] imageChunk;
                if ((i + MAX_CHUNK_SIZE) < length) {
                    imageChunk = Arrays.copyOfRange(image, i, (int) (MAX_CHUNK_SIZE));
                } else {
                    imageChunk = Arrays.copyOfRange(image, i, length);
                }
                imageChunks.add(Blob.fromBytes(imageChunk));
                ++chunk;
            }
        } else {
            imageChunks.add(Blob.fromBytes(image));
        }
        return imageChunks;
    }
}
