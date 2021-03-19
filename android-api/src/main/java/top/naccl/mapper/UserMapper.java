package top.naccl.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import top.naccl.model.entity.User;

/**
 * @Description: 用户持久层接口
 * @Author: Naccl
 * @Date: 2020-10-16
 */
@Mapper
@Repository
public interface UserMapper {
	User findByUsername(String username);

	int saveUser(User user);

	int updateUserById(User user, Long id);
}
