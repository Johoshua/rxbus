import org.springframework.web.client.RestTemplate;

public class RestEvents {

    public static void retrieveGitEvents()
    {
        RestTemplate restTemplate = new RestTemplate();
        String res = restTemplate.getForObject("https://api.github.com/events", String.class);

        System.out.println(res);
    }
}
