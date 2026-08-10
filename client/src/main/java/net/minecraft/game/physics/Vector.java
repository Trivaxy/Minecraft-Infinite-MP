package net.minecraft.game.physics;

import net.minecraft.util.math.MathHelper;

public class Vector {
    public double xCoord;
    public double yCoord;
    public double zCoord;

    public static Vector createVector(double x, double y, double z) {
        return new Vector(x, y, z);
    }

    public Vector(double x, double y, double z) {
        if (x == -0.0) {
            x = 0.0;
        }

        if (y == -0.0) {
            y = 0.0;
        }

        if (z == -0.0) {
            z = 0.0;
        }

        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    protected Vector setCoords(double x, double y, double z) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        return this;
    }

    public Vector subtract(Vector vector) {
        return createVector(vector.xCoord - this.xCoord, vector.yCoord - this.yCoord, vector.zCoord - this.zCoord);
    }

    public Vector add(Vector vector) {
        return createVector(vector.xCoord + this.xCoord, vector.yCoord + this.yCoord, vector.zCoord + this.zCoord);
    }

    public Vector normalize() {
        double dist = MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
        return dist < 1.0E-4 ? createVector(0.0, 0.0, 0.0) : createVector(this.xCoord / dist, this.yCoord / dist, this.zCoord / dist);
    }

    public Vector crossProduct(Vector vector) {
        return createVector(
            this.yCoord * vector.zCoord - this.zCoord * vector.yCoord,
            this.zCoord * vector.xCoord - this.xCoord * vector.zCoord,
            this.xCoord * vector.yCoord - this.yCoord * vector.xCoord
        );
    }

    public Vector addVector(double x, double y, double z) {
        return createVector(this.xCoord + x, this.yCoord + y, this.zCoord + z);
    }

    public double distanceTo(Vector vector) {
        double x = vector.xCoord - this.xCoord;
        double y = vector.yCoord - this.yCoord;
        double z = vector.zCoord - this.zCoord;
        return MathHelper.sqrt_double(x * x + y * y + z * z);
    }

    public double squareDistanceTo(Vector vector) {
        double x = vector.xCoord - this.xCoord;
        double y = vector.yCoord - this.yCoord;
        double z = vector.zCoord - this.zCoord;
        return x * x + y * y + z * z;
    }

    public double lengthVector() {
        return MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
    }

    public Vector getIntermediateX(Vector vector, double val) {
        double x = vector.xCoord - this.xCoord;
        double y = vector.yCoord - this.yCoord;
        double z = vector.zCoord - this.zCoord;
        if (x * x < 1.0E-7F) {
            return null;
        }

        double dist = (val - this.xCoord) / x;
        return dist >= 0.0 && dist <= 1.0 ? createVector(this.xCoord + x * dist, this.yCoord + y * dist, this.zCoord + z * dist) : null;
    }

    public Vector getIntermediateY(Vector vector, double val) {
        double x = vector.xCoord - this.xCoord;
        double y = vector.yCoord - this.yCoord;
        double z = vector.zCoord - this.zCoord;
        if (y * y < 1.0E-7F) {
            return null;
        }

        double dist = (val - this.yCoord) / y;
        return dist >= 0.0 && dist <= 1.0 ? createVector(this.xCoord + x * dist, this.yCoord + y * dist, this.zCoord + z * dist) : null;
    }

    public Vector getIntermediateZ(Vector vector, double val) {
        double x = vector.xCoord - this.xCoord;
        double y = vector.yCoord - this.yCoord;
        double z = vector.zCoord - this.zCoord;
        if (z * z < 1.0E-7F) {
            return null;
        }

        double dist = (val - this.zCoord) / z;
        return dist >= 0.0 && dist <= 1.0 ? createVector(this.xCoord + x * dist, this.yCoord + y * dist, this.zCoord + z * dist) : null;
    }

    public double getLocationCoords(Vector vector) {
        return this.xCoord * vector.xCoord + this.yCoord * vector.yCoord + this.zCoord * vector.zCoord;
    }

    public void rotateAroundX(float angle) {
        double pitch = MathHelper.cos(angle);
        double yaw = MathHelper.sin(angle);
        double x = this.xCoord;
        double y = this.yCoord * pitch + this.zCoord * yaw;
        double z = this.zCoord * pitch - this.yCoord * yaw;
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    public void rotateAroundY(float angle) {
        double pitch = MathHelper.cos(angle);
        double yaw = MathHelper.sin(angle);
        double x = this.xCoord * pitch + this.zCoord * yaw;
        double y = this.yCoord;
        double z = this.zCoord * pitch - this.xCoord * yaw;
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    public void rotateAroundZ(float angle) {
        double pitch = MathHelper.cos(angle);
        double yaw = MathHelper.sin(angle);
        double x = this.xCoord * pitch + this.yCoord * yaw;
        double y = this.yCoord * pitch - this.xCoord * yaw;
        double z = this.zCoord;
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    public static double dotProduct(Vector a, Vector b) {
        return a.xCoord * b.xCoord + a.yCoord * b.yCoord + a.zCoord * b.zCoord;
    }

    public double dotProduct(Vector vector) {
        return this.xCoord * vector.xCoord + this.yCoord * vector.yCoord + this.zCoord * vector.zCoord;
    }

    public Vector scale(double amount) {
        return new Vector(this.xCoord * amount, this.yCoord * amount, this.zCoord * amount);
    }

    @Override
    public String toString() {
        return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
    }
}
