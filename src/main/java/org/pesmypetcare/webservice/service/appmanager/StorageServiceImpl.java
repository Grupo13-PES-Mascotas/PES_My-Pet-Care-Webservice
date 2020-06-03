package org.pesmypetcare.webservice.service.appmanager;

import org.pesmypetcare.webservice.dao.appmanager.StorageDao;
import org.pesmypetcare.webservice.dao.communitymanager.GroupDao;
import org.pesmypetcare.webservice.entity.appmanager.ImageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.form.StorageForm;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserToken;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserTokenImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Santiago Del Rey
 */
@Service
public class StorageServiceImpl implements StorageService {
    @Autowired
    private StorageDao storageDao;
    @Autowired
    private GroupDao groupDao;
    private UserToken userToken;

    public UserToken makeUserToken(String token) {
        return new UserTokenImpl(token);
    }

    @Override
    public void saveUserImage(ImageEntity image) {
        storageDao.uploadImage(image);
    }

    @Override
    public void savePetImage(String token, ImageEntity image) throws DatabaseAccessException, DocumentException {
        storageDao.uploadPetImage(makeUserToken(token).getUsername(), image);
    }

    @Override
    public void saveGroupImage(String token, String group, ImageEntity image)
        throws DatabaseAccessException, DocumentException {
        userToken = makeUserToken(token);
        if (!userToken.getUsername().equals(groupDao.getGroup(group).getCreator())) {
            throw new BadCredentialsException("The user is not the creator of the group");
        }
        if (!groupDao.groupNameInUse(group)) {
            throw new DocumentException("document-not-exists", "The group does not exist.");
        }
        storageDao.uploadGroupImage(userToken, image);
    }

    @Override
    public String getImage(StorageForm form) {
        return storageDao.downloadImage(form);
    }

    @Override
    public Map<String, String> getAllPetImages(String owner) throws DatabaseAccessException, DocumentException {
        return storageDao.downloadAllPetImages(owner);
    }

    @Override
    public Map<String, String> getAllPostsImagesFromForum(String group, String forum)
        throws DatabaseAccessException, DocumentException {
        return storageDao.downloadAllPostsImagesFromForum(group, forum);
    }

    @Override
    public void deleteImage(StorageForm form) {
        storageDao.deleteImage(form);
    }
}
