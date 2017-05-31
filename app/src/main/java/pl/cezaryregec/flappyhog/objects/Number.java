package pl.cezaryregec.flappyhog.objects;

public class Number {

    public static final int[][] NUMBER_TABLE = {
            {0, 0}, {1, 0}, {2, 0}, {3, 0},  // 0, 1, 2, 3,
            {0, 1}, {1, 1}, {2, 1}, {3, 1},  // 4. 5. 6. 7,
            {0, 2}, {1, 2}, {2, 2}           // 8, 9, -
    };

    public float[] position = { 0.0f, 0.0f, 0.0f };
    public float[] scale = { 1.0f, 1.0f, 1.0f };
    public float[] color = { 1.0f, 1.0f, 1.0f, 1.0f };

    private String number;
    private Sprite[] digits;
    private int mTextureHandler;

    public Number(int number, int texture) {
        this.number = "" + number;
        mTextureHandler = texture;

        // prepare digits array
        digits = new Sprite[this.number.length()];

        // init every digit
        for(int i = 0; i < digits.length; i++) {
            digits[i] = new Sprite(mTextureHandler);

            // get correct digit
            if(this.number.charAt(i) == '-') {

                digits[i].textureBlock(NUMBER_TABLE[11][0], NUMBER_TABLE[11][1], 4, 4); // -

            } else {

                int n = Integer.parseInt("" + this.number.charAt(i));
                digits[i].textureBlock(NUMBER_TABLE[n][0], NUMBER_TABLE[n][1], 4, 4); // 'n' digit

            }
        }

        update();
    }

    public void update(float size) {
        // update size
        scale = new float[] { size * digits.length, size, 0.0f };

        update();
    }

    public void update() {
        // get size
        float digit_width = scale[0] / digits.length;
        float digit_height = scale[1];

        for(int i = 0; i < digits.length; i++) {
            // calculate horizontal position
            float h_position = position[0] + (scale[0] / 2.0f) - digit_width * (digits.length - i);

            // update position and scale
            digits[i].position = new float[]{ h_position, position[1], position[2] };
            digits[i].scale = new float[] { digit_width, digit_height, 1.0f };

            // don't forget about color
            digits[i].color = color;
        }
    }

    public void draw(float[] mMVPMatrix) {
        for(Sprite digit : digits) {
            digit.draw(mMVPMatrix);
        }
    }

}
