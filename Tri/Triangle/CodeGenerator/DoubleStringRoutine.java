package Triangle.CodeGenerator;

public class DoubleStringRoutine extends RuntimeEntity {

  public DoubleStringRoutine () {
    super();
  }

  public DoubleStringRoutine (int size, int displacement) {
    super (size);
    this.displacement = displacement;
  }

  public int displacement;
}