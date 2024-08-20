package URL.Shortner.Assignment.Controller;


import URL.Shortner.Assignment.Entity.UrlEntity;
import URL.Shortner.Assignment.Services.UrlShortenerServices;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/url")
@Slf4j
public class UrlShortenerController {
    @Autowired
    private UrlShortenerServices urlShortenerServices;

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@RequestBody Map<String, String> request) {
        String originalUrl = request.get("destinationUrl");
        UrlEntity urlEntity = urlShortenerServices.createShortUrl(originalUrl);
        return ResponseEntity.ok(Map.of("shortUrl", "http://localhost:8080/" + urlEntity.getShortUrl(), "id", urlEntity.getId()));
    }

    @GetMapping("/{shortenString}")
    public ResponseEntity<?> redirectToFullUrl(@PathVariable String shortenString) {
        try {
            // Fetch the URL entity
            UrlEntity urlEntity = urlShortenerServices.getOriginalUrl(shortenString)
                    .filter(entity -> entity.getExpiresAt().isAfter(LocalDateTime.now()))
                    .orElseThrow(() -> new NoSuchElementException("Url not found or expired"));

           // Create the headers
//            HttpHeaders headers = new HttpHeaders();
//            URI locationUri = URI.create(urlEntity.getOriginalUrl());
//
            // Log the URL being redirected to
//            log.info("Redirecting to: {}", locationUri);
//
//            headers.setLocation(locationUri);

            //for testing purpose in postman
            String headers = urlEntity.getOriginalUrl();

            // Return the response with a redirect status
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (NoSuchElementException e) {
            log.error("URL not found or expired: {}", shortenString);
            return new ResponseEntity<>("URL not found or expired", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Unexpected error occurred", e);
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/update")
    public ResponseEntity<?> updateShortUrl(@RequestBody Map<String, String> request) {
        String shortUrl = request.get("shortUrl");
        String newOriginalUrl = request.get("destinationUrl");
        boolean success = urlShortenerServices.updateDestinationUrl(shortUrl, newOriginalUrl);
        return new ResponseEntity<>(success,HttpStatus.OK);
    }

    @PutMapping("/update-expiry")
    public ResponseEntity<?> updateExpiry(@RequestBody Map<String, Object> request) {
        String shortUrl = (String) request.get("shortUrl");
        int daysToAdd = (int) request.get("daysToAdd");
        boolean success = urlShortenerServices.updateExpiry(shortUrl, daysToAdd);
        return ResponseEntity.ok(Map.of("success", success));
    }
}
