package org.pesmypetcare.webservice.entity.communitymanager;

import lombok.Data;

import java.util.Map;
import java.util.Objects;

/**
 * @author Santiago Del Rey
 */
@Data
public class Forum extends ForumEntity {
    private Map<String, String> members;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Forum forum = (Forum) o;
        return Objects.equals(members, forum.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), members);
    }
}
