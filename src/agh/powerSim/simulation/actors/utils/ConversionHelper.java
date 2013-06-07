package agh.powerSim.simulation.actors.utils;

/**
 *
 * Created with IntelliJ IDEA.
 * User: Khajiit
 * Date: 07.06.13
 * Time: 15:55
 *
 * Class responsible for conversion from string to given type
 */
public class ConversionHelper {

    /**
     * Tries to return given String value as an object of specified type.
     *
     * If an object is not Double or Integer String is returned.
     *
     * @param value
     * @param type
     * @return
     */
    public static Object convert(String value, Class<?> type) {
        if ( type.isInstance(new Integer("0")) || "integer".equals(type.getSimpleName()) ) {
            return new Integer(value);
        } else if ( type.isInstance(new Double("0")) || "double".equals(type.getSimpleName()) ) {
            return new Double(value);
        } else {
            return new String(value);
        }
    }
}
