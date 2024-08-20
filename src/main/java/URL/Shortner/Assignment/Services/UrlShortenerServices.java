package URL.Shortner.Assignment.Services;

import URL.Shortner.Assignment.Entity.UrlEntity;
import URL.Shortner.Assignment.Repository.UrlShortenerRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class UrlShortenerServices {

    @Autowired
    private UrlShortenerRepository urlShortenerRepository;

    public UrlEntity createShortUrl(String originalUrl){
        // Generate a unique short URL
        String shortUrl = generateUniqueShortUrl();

        // Get the current timestamp
        LocalDateTime now = LocalDateTime.now();

        // Calculate the expiration timestamp (10 months from now)
        LocalDateTime expiresAt = now.plus(10, ChronoUnit.MONTHS);

        // Create a new UrlEntity object
        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setShortUrl(shortUrl);
        urlEntity.setOriginalUrl(originalUrl);
        urlEntity.setCreatedAt(now);
        urlEntity.setExpiresAt(expiresAt);

        // Save the UrlEntity object using the repository
        return urlShortenerRepository.save(urlEntity);
    }

    public Optional<UrlEntity> getOriginalUrl(String shortUrl) {
        return urlShortenerRepository.findByShortUrl(shortUrl);
    }

    public  boolean updateDestinationUrl(String shortUrl, String newOriginalUrl){
        Optional<UrlEntity> urlEntityOpt = urlShortenerRepository.findByShortUrl(shortUrl);
        if(urlEntityOpt.isPresent()){
            UrlEntity urlEntity = urlEntityOpt.get();
            urlEntity.setOriginalUrl(newOriginalUrl);
            urlShortenerRepository.save(urlEntity);
            return true;
        }
        return false;
    }

    public  boolean updateExpiry(String shortUrl, int daysToAdd){
        Optional<UrlEntity> urlEntityOpt = urlShortenerRepository.findByShortUrl(shortUrl);
        if(urlEntityOpt.isPresent()){
            UrlEntity urlEntity = urlEntityOpt.get();
            urlEntity.setExpiresAt(urlEntity.getExpiresAt().plus(daysToAdd, ChronoUnit.DAYS));
            urlShortenerRepository.save(urlEntity);
            return true;
        }
        return false;
    }

    private String generateUniqueShortUrl(){
        return RandomStringUtils.randomAlphanumeric(8);
    }
}
