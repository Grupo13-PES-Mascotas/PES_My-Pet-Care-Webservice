package org.pesmypetcare.webservice.entity.communitymanager;

import com.google.cloud.firestore.Blob;
import lombok.Data;
import org.pesmypetcare.webservice.utilities.Splitter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

/**
 * @author Santiago Del Rey
 */
@Data
public class MessageEntity {
    private String creator;
    private String publicationDate;
    private String text;
    private List<Blob> image;
    private boolean banned;
    private List<String> likedBy;

    public MessageEntity() { };

    /**
     * Creates a message entity with the given message
     * @param message The message
     */
    public MessageEntity(Message message) {
        this.creator = message.getCreator();
        this.text = message.getText();
        this.likedBy = new ArrayList<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", new Locale("es", "ES"));
        this.publicationDate = timeFormatter.format(LocalDateTime.now());
        this.image = decodeAndSplitImage(message.getEncodedImage());
    }

    /**
     * Decodes a base 64 encoded image and splits it in chunks.
     * @param encodedImage The base 64 encoded image
     * @return A list with the chunks
     */
    private List<Blob> decodeAndSplitImage(String encodedImage) {
        System.out.println(encodedImage.length());
        List<Blob> image = new ArrayList<>();
        if (encodedImage != null) {
            if (!encodedImage.isEmpty()) {
                image = Splitter.splitImage(Base64.getDecoder().decode(encodedImage));
            }
        }
        System.out.println(image.size());
        return image;
    }
}
