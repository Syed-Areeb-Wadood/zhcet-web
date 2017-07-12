package in.ac.amu.zhcet.data.model.base;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@DynamicUpdate
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "password")
public class BaseUser extends BaseEntity {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Id
    private String userId;

    private String password;
    private String[] roles;

    private String name;

    public BaseUser(String userId, String password, String name, String[] roles) {
        setUserId(userId);
        setPassword(password);
        setName(name);
        setRoles(roles);
    }
}