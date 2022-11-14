package lu.itrust.business.TS.model.ilr;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import javax.persistence.Embeddable;

@Embeddable
public class Position {

    private double x;

    private double y;

    public Position() {
    }

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }


    public static Position generate(int size) throws NoSuchAlgorithmException {
        final SecureRandom random = SecureRandom.getInstanceStrong();
       return generate(size, random);

    }

    public static Position generate(int size, Random random){
        final double min = size * 10d;
        final double max = min * 2d + 1d;
        return new Position(random.nextDouble() * max - min, random.nextDouble() * max - min);

    }
}
