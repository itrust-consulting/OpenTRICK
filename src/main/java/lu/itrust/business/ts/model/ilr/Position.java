package lu.itrust.business.ts.model.ilr;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import jakarta.persistence.Embeddable;

/**
 * Represents a position in a two-dimensional space.
 */
@Embeddable
public class Position {

    private double x;
    private double y;

    /**
     * Constructs a new Position object with default coordinates (0, 0).
     */
    public Position() {
    }

    /**
     * Constructs a new Position object with the specified coordinates.
     *
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     */
    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x-coordinate of the position.
     *
     * @return The x-coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the position.
     *
     * @param x The new x-coordinate.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Returns the y-coordinate of the position.
     *
     * @return The y-coordinate.
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the position.
     *
     * @param y The new y-coordinate.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Generates a random Position object within a specified size range.
     *
     * @param size The size range for generating random coordinates.
     * @return A randomly generated Position object.
     * @throws NoSuchAlgorithmException If a secure random algorithm is not available.
     */
    public static Position generate(int size) throws NoSuchAlgorithmException {
        final SecureRandom random = SecureRandom.getInstanceStrong();
        return generate(size, random);
    }

    /**
     * Generates a random Position object within a specified size range using the provided random generator.
     *
     * @param size   The size range for generating random coordinates.
     * @param random The random generator to use.
     * @return A randomly generated Position object.
     */
    public static Position generate(int size, Random random) {
        final double min = size * 10d;
        final double max = min * 2d + 1d;
        return new Position(random.nextDouble() * max - min, random.nextDouble() * max - min);
    }
}
