package org.pesmypetcare.webservice.form;

import lombok.Data;

@Data
public class StorageForm {
    private String path;
    private String imageName;

    public StorageForm() {
        path = "";
        imageName = "";
    }

    /**
     * Creates a storage form with the given values.
     * @param path The path to the image
     * @param imageName The image name
     */
    public StorageForm(String path, String imageName) {
        this.path = path;
        this.imageName = imageName;
    }
}
