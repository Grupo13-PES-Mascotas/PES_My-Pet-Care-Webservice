package org.pesmypetcare.webservice.thirdpartyservices.adapters;

/**
 * @author Santiago Del Rey
 */
public interface UserToken {
    /**
     * Returns the user's username.
     *
     * @return The user's username
     */
    String getUsername();

    /**
     * Returns the user's email.
     *
     * @return The user's email
     */
    String getEmail();

    /**
     * Returns the user's UID.
     *
     * @return The user's UID
     */
    String getUid();
}
