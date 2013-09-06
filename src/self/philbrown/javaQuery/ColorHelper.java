package self.philbrown.javaQuery;

import java.awt.Color;
import java.util.HashMap;
import java.util.Locale;

/**
 * Helper class for making and manipulating Colors
 * @author Phil Brown
 * @since 12:05:21 PM Sep 6, 2013
 *
 */
public class ColorHelper
{
	private int red, green, blue, alpha;
	
	public ColorHelper()
	{
	}
	
	public ColorHelper(Color color)
	{
		if (color != null)
		{
			this.red = color.getRed();
			this.green = color.getGreen();
			this.blue = color.getBlue();
			this.alpha = color.getAlpha();
		}
	}
	public ColorHelper(int r, int g, int b, int a)
	{
		red = r;
		blue = b;
		green = g;
		alpha = a;
	}
	public int getRed() {
		return red;
	}
	public void setRed(int red) {
		this.red = red;
	}
	public int getGreen() {
		return green;
	}
	public void setGreen(int green) {
		this.green = green;
	}
	public int getBlue() {
		return blue;
	}
	public void setBlue(int blue) {
		this.blue = blue;
	}
	public int getAlpha() {
		return alpha;
	}
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	public Color getColor()
	{
		return new Color(red, blue, green, alpha);
	}
	
	///Colors code from android.graphics.Color.java
	
	public static final int BLACK       = 0xFF000000;
    public static final int DKGRAY      = 0xFF444444;
    public static final int GRAY        = 0xFF888888;
    public static final int LTGRAY      = 0xFFCCCCCC;
    public static final int WHITE       = 0xFFFFFFFF;
    public static final int RED         = 0xFFFF0000;
    public static final int GREEN       = 0xFF00FF00;
    public static final int BLUE        = 0xFF0000FF;
    public static final int YELLOW      = 0xFFFFFF00;
    public static final int CYAN        = 0xFF00FFFF;
    public static final int MAGENTA     = 0xFFFF00FF;
    public static final int TRANSPARENT = 0;
    
	private static final HashMap<String, Integer> sColorNameMap;

    static {
        sColorNameMap = new HashMap<String, Integer>();
        sColorNameMap.put("black", BLACK);
        sColorNameMap.put("darkgray", DKGRAY);
        sColorNameMap.put("gray", GRAY);
        sColorNameMap.put("lightgray", LTGRAY);
        sColorNameMap.put("white", WHITE);
        sColorNameMap.put("red", RED);
        sColorNameMap.put("green", GREEN);
        sColorNameMap.put("blue", BLUE);
        sColorNameMap.put("yellow", YELLOW);
        sColorNameMap.put("cyan", CYAN);
        sColorNameMap.put("magenta", MAGENTA);
    }
	
	/**
	 * Derived from Android's color class.<br>
     * Parse the color string, and return the corresponding color-int.
     * If the string cannot be parsed, throws an IllegalArgumentException
     * exception. Supported formats are:
     * #RRGGBB
     * #AARRGGBB
     * 'red', 'blue', 'green', 'black', 'white', 'gray', 'cyan', 'magenta',
     * 'yellow', 'lightgray', 'darkgray'
     */
    public static Color parseColor(String colorString) {
        if (colorString.charAt(0) == '#') {
            // Use a long to avoid rollovers on #ffXXXXXX
            long color = Long.parseLong(colorString.substring(1), 16);
            if (colorString.length() == 7) {
                // Set the alpha value
                color |= 0x00000000ff000000;
            } else if (colorString.length() != 9) {
                throw new IllegalArgumentException("Unknown color");
            }
            int a = (int) (color >> 24) & 0xFF;
            int r = (int) (color >> 16) & 0xFF;
            int g = (int) (color >> 8) & 0xFF;
            int b = (int) color & 0xFF;
            return new Color(r,g,b,a);
        } else {
            Integer color = sColorNameMap.get(colorString.toLowerCase(Locale.US));
            if (color != null) {
            	int a = (int) (color >> 24) & 0xFF;
            	int r = (int) (color >> 16) & 0xFF;
            	int g = (int) (color >> 8) & 0xFF;
            	int b = (int) color & 0xFF;
                return new Color(r,g,b,a);
            }
        }
        throw new IllegalArgumentException("Unknown color");
    }
}