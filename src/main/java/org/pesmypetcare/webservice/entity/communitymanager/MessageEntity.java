package org.pesmypetcare.webservice.entity.communitymanager;

import com.google.cloud.firestore.Blob;
import lombok.Data;
import org.pesmypetcare.webservice.utilities.UTCLocalConverter;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author Santiago Del Rey
 */
@Data
public class MessageEntity {
    private String creator;
    private String publicationDate;
    private String text;
    private boolean banned;
    private List<String> reportedBy;
    private List<String> likedBy;
    private Blob image;

    public MessageEntity() { }

    /**
     * Creates a message entity with the given message.
     * @param message The message
     */
    public MessageEntity(Message message) {
        this.creator = message.getCreator();
        this.publicationDate = UTCLocalConverter.getCurrentUTC();
        this.text = message.getText();
        this.banned = false;
        this.reportedBy = new ArrayList<>();
        this.likedBy = new ArrayList<>();
        this.image = decodeImage(message.getEncodedImage());
    }

    /**
     * Decodes a base 64 encoded image.
     * @param encodedImage The base 64 encoded image
     * @return A blob with the image bytes
     */
    private Blob decodeImage(String encodedImage) {
        if (encodedImage != null) {
            if (!encodedImage.isEmpty()) {
                return Blob.fromBytes(Base64.getDecoder().decode(encodedImage));
            }
        }
        return null;
    }
}
