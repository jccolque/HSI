package ar.lamansys.sgx.auth.user.infrastructure.output.user;

import ar.lamansys.sgx.auth.user.domain.user.model.UserBo;
import ar.lamansys.sgx.auth.user.domain.user.service.UserStorage;
import ar.lamansys.sgx.auth.user.domain.user.service.exceptions.UserStorageEnumException;
import ar.lamansys.sgx.auth.user.domain.user.service.exceptions.UserStorageException;
import ar.lamansys.sgx.auth.user.infrastructure.output.userpassword.UserPassword;
import ar.lamansys.sgx.auth.user.infrastructure.output.userpassword.UserPasswordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserStorageImpl implements UserStorage {

    public static final String PASSWORD_CON_ID_INEXISTENTE = "Password con id (%s) inexistente";
    private final Logger logger;

    private final UserRepository userRepository;

    private final UserPasswordRepository userPasswordRepository;

    public UserStorageImpl(UserRepository userRepository,
                           UserPasswordRepository userPasswordRepository) {
        this.logger =  LoggerFactory.getLogger(UserStorageImpl.class);
        this.userPasswordRepository = userPasswordRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void save(UserBo user) {
        logger.debug("Save user in repository -> {}", user);
        User userEntity = mapUser(user);
        userEntity = userRepository.save(userEntity);
        userPasswordRepository.save(mapUserPassword(user, userEntity));
    }

    @Override
    public void update(UserBo userBo) {
        logger.debug("Save user in repository -> {}", userBo);
        User user = userRepository.findById(userBo.getId())
                .orElseThrow(() -> new UserStorageException(UserStorageEnumException.NOT_FOUND_USER,
                        String.format("Usuario con id (%s) inexistente", userBo)));
        UserPassword userPassword = userPasswordRepository.findById(userBo.getId())
                .orElseThrow(() -> new UserStorageException(UserStorageEnumException.NOT_FOUND_USER_PASSWORD,
                        String.format(PASSWORD_CON_ID_INEXISTENTE, userBo.getId())));

        updateUser(userBo, user);
        userRepository.save(user);

        updateUserPassword(userBo, userPassword);
        userPasswordRepository.save(userPassword);
    }

    private UserPassword updateUserPassword(UserBo userBo, UserPassword userPassword) {
        userPassword.setPassword(userBo.getPassword());
        userPassword.setSalt(userBo.getSalt());
        userPassword.setHashAlgorithm(userBo.getHashAlgorithm());
        return userPassword;
    }

    private User updateUser(UserBo userBo, User user) {
        user.setUsername(userBo.getUsername());
        user.setEnable(userBo.isEnable());
        user.setLastLogin(userBo.getLastLogin());
        return user;
    }

    @Override
    public UserBo getUser(Integer userId) {
        logger.debug("Get user in repository -> {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserStorageException(UserStorageEnumException.NOT_FOUND_USER,
                        String.format("Usuario con id (%s) inexistente", userId)));
        UserPassword userPassword = userPasswordRepository.findById(userId)
                .orElseThrow(() -> new UserStorageException(UserStorageEnumException.NOT_FOUND_USER_PASSWORD,
                        String.format(PASSWORD_CON_ID_INEXISTENTE, userId)));
        return mapUserBo(user, userPassword);
    }

    @Override
    public UserBo getUser(String username) {
        logger.debug("Get user in repository -> {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserStorageException(UserStorageEnumException.NOT_FOUND_USER,
                        String.format("Usuario con username (%s) inexistente", username)));
        UserPassword userPassword = userPasswordRepository.findById(user.getId())
                .orElseThrow(() -> new UserStorageException(UserStorageEnumException.NOT_FOUND_USER_PASSWORD,
                        String.format(PASSWORD_CON_ID_INEXISTENTE, user.getId())));
        return mapUserBo(user, userPassword);
    }


    private UserBo mapUserBo(User user, UserPassword userPassword) {
        return new UserBo(
                user.getId(),
                user.getUsername(),
                user.getEnable(),
                userPassword.getPassword(),
                userPassword.getSalt(),
                userPassword.getHashAlgorithm(),
                user.getLastLogin());
    }

    private UserPassword mapUserPassword(UserBo user, User userEntity) {
        UserPassword result = new UserPassword();
        result.setId(userEntity.getId());
        result.setPassword(user.getPassword());
        result.setSalt(user.getSalt());
        result.setHashAlgorithm(user.getHashAlgorithm());

        return result;
    }

    private User mapUser(UserBo user) {
        User result = new User();
        result.setId(user.getId());
        result.setUsername(user.getUsername());
        result.setEnable(user.isEnable());
        result.setLastLogin(user.getLastLogin());
        return result;
    }
}