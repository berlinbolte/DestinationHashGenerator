import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
	
public class DestinationHashGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <RollNumber> <JSONFilePath>");
            return;
        }

        String rollNumber = args[0].toLowerCase().replaceAll("\\s", ""); 
        String jsonFilePath = args[1];

        try {
            String destinationValue = findDestinationValue(jsonFilePath);
            String randomString = generateRandomString(8);
            String concatenatedString = rollNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(concatenatedString);
            System.out.println(md5Hash + ";" + randomString);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String findDestinationValue(String jsonFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(jsonFilePath));

        return traverseJson(root);
    }

    private static String traverseJson(JsonNode node) {
        if (node.isObject()) {
            // Iterate over the fields of the JSON object
            var iterator = node.fields(); // Returns an Iterator<Map.Entry<String, JsonNode>>
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> field = iterator.next(); // Explicitly define the type
                if (field.getKey().equals("destination")) {
                    return field.getValue().asText();
                }
                String result = traverseJson(field.getValue()); // Recursively check nested structures
                if (result != null) return result;
            }
        } else if (node.isArray()) {
            // Iterate over the elements of the JSON array
            for (JsonNode element : node) {
                String result = traverseJson(element);
                if (result != null) return result;
            }
        }
        return null; 
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
	}

}
