package photon.mfw.model;

import org.apache.ibatis.annotations.Param;

/**
 * Created by dan on 29/10/2016.
 */
public interface UserMapper {
    void init(@Param("userId") Integer userId);

    void insert(User user);

    Integer find(@Param("name") String name);
}
