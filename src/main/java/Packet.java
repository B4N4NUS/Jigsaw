public class Packet {
    enum Type{
        FIGURE,
        SCORE,
        TIME,
        NAME,
        DISCONNECTION
    }

    public static Object unwrap(String text) {
        String[] data = text.split(" ");
        switch (Integer.parseInt(data[0])) {
            case 0: {
                return Figure.figures[Integer.parseInt(text.split(" ")[1])];
            }
            case 1:
            case 2: {
                return Integer.parseInt(data[1]);
            }
            case 3: {
                return data[1];
            }
            case 4: {

                break;
            }
            case 5: {

                break;
            }
            default: {
                return null;
            }
        }
        return null;
    }

    public static String wrap(Object obj, Type type) {
        switch (type) {
            case FIGURE: {
                return "0 " + obj;
            }
            case SCORE: {
                return "1 " + obj;
            }
            case TIME: {
                return "2 " + obj;
            }
            case NAME: {
                return "3 " + obj;
            }
            case DISCONNECTION: {
                return "4 " + obj;
            }
        }
        return "100 " + obj;
    }
}
