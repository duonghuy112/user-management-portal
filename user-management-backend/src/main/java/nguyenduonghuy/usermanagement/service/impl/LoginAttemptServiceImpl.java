package nguyenduonghuy.usermanagement.service.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import nguyenduonghuy.usermanagement.service.LoginAttemptService;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {

	private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
	private static final int ATTEMPT_INCREMENT = 1;
	private LoadingCache<String, Integer> loginAttemptCache;

	public LoginAttemptServiceImpl() {
		loginAttemptCache = CacheBuilder.newBuilder()
										.expireAfterWrite(15, TimeUnit.MINUTES)
										.maximumSize(50)
										.build(new CacheLoader<String, Integer>() {
											@Override
											public Integer load(String key) throws Exception {
												return 0;
											}
										});
	}

	public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }

	public void addUserToLoginAttemptCache(String username) {
        int attempts = 0;
        try {
            attempts = loginAttemptCache.get(username) + ATTEMPT_INCREMENT;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        loginAttemptCache.put(username, attempts);
    }

    public boolean hasExceededMaxAttempts(String username) {
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
