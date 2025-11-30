package processing;

public class ObjectProcessor {

    public String process(Object obj) {
        System.out.println("Starting validation...");

        try {
            Validator.validate(obj);
            System.out.println("Validation successful.");

            System.out.println("Starting JSON serialization...");
            String json = JsonSerializer.serialize(obj);
            System.out.println("Serialization successful.");

            return json;

        } catch (ValidationException e) {
            System.err.println("Validation error: " + e.getMessage());
            return "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}";
        } catch (IllegalAccessException e) {
            System.err.println("Internal reflection error: " + e.getMessage());
            return "{\"error\":\"internal server error\"}";
        }
    }

    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
