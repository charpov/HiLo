package injava;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.Validate;

import java.util.Optional;
import java.util.Scanner;

import static java.lang.Math.log;

/**
 * A Hi-Lo game.
 * The computer guesses the user's secret number by asking questions.
 * <p>
 * Note that only the methods `yes()` and `no()` have side effects. Other methods can be called
 * repeatedly and will produce the same values until `yes()` or `no()` is called.
 *
 * @author Michel Charpentier
 * @implNote Contrast with the Scala implementation.
 */
public final class HiLo {
  private int low;
  private int high;
  private final int size;

  /**
   * Primary constructor.
   *
   * @param min lower bound of the range of possible secret numbers; cannot negative.
   * @param max upper bound of the range of possible secret numbers; cannot be more than 999_999_999.
   * @throws IllegalArgumentException if the specified range is empty, parameter 'min' is negative,
   *                                  or parameter 'max' is larger than 999_999_999.
   */
  public HiLo(int min, int max) {
    Validate.isTrue(min <= max, "range cannot be empty");
    Validate.isTrue(min >= 0, "min cannot be negative");
    Validate.isTrue(max < 1_000_000_000, "max must be less than 1_000_000_000");

    low  = min;
    high = max;
    size = max - min + 1;
  }

  /**
   * Creates an instance of {@link HiLo} with range {@code 1..max}.
   * 
   * @throws IllegalArgumentException if parameter 'max' is less than 1 or greater than 999_999_999.
   */
  public static HiLo upto(int max) {
    return new HiLo(1, max);
  }

  private int midpoint() {
    return (low + high) / 2;
  }
  
  /**
   * The question to the user, as a range.
   * <p>
   * Semantically, the question is: Is your secret number within this range?
   * It is illegal to call this method if the problem has already been solved.
   *
   * @throws IllegalStateException if the problem has already been solved.
   */
  public Range<Integer> choices() {
    Validate.validState(!solved(), "problem has been solved already");
    assert low <= midpoint();
    return Range.of(low, midpoint());
  }

  /**
   * Termination.
   * <p>
   * That is, whether the problem has been solved. Note that a problem might be solved without any
   * user interaction (if the initial range is reduced to one value).
   */
  public boolean solved() {
    return low == high;
  }

  /**
   * 'Yes' answer.
   * <p>
   * This method is called by the user to indicate that the secret number is present in the
   * proposed range. It generates a new question as a side effect, unless the problem is solved.
   * It is illegal to call this method if the problem has already been solved.
   *
   * @throws IllegalStateException if the problem has already been solved.
   */
  public void yes() {
    Validate.validState(!solved(), "problem has been solved already");
    high = midpoint();
    assert low <= high;
  }

  /**
   * 'No' answer.
   * <p>
   * This method is called by the user to indicate that the secret number is not present in the
   * proposed range. It generates a new question as a side effect, unless the problem is solved.
   * It is illegal to call this method if the problem has already been solved.
   *
   * @throws IllegalStateException if the problem has already been solved.
   */
  public void no() {
    Validate.validState(!solved(), "problem has been solved already");
    low = midpoint() + 1;
    assert low <= high;
  }

  /**
   * Progress towards guessing.
   * <p>
   * The contract for this method is as follows:
   * <p>
   * - progress is exactly 1.0 when a problem is solved;
   * - progress is exactly 0.0 initially, unless the problem is solved (in which case, it is 1.0);
   * - progress increases after each call to `yes` or `no` until the problem is solved.
   * <p>
   * Good progress methods are based on the ''logarithm'' of the size of the range of possible
   * numbers (since the number of steps required to solve the problem by dichotomy is in the order
   * of the logarithm of the size), but simpler functions (that increase slowly before they jump)
   * can be devised that also satisfy the contract.
   */
  public double progress() {
    assert size > 0;
    return solved() ? 1.0 : 1 - log(high - low + 1) / log(size);
  }

  /**
   * The secret number.
   * <p>
   * This method can only be called after the problem has been solved.
   *
   * @throws IllegalStateException if called before the problem has been solved.
   */
  public int secret() {
    Validate.validState(solved(), "problem not solved yet");
    assert low == high;
    return low;
  }

  private static Optional<Boolean> yesOrNo(String line) {
    return switch (line.trim()) {
      case "y", "Y", "yes", "Yes", "YES" -> Optional.of(true);
      case "n", "N", "no", "No", "NO" -> Optional.of(false);
      default -> Optional.empty();
    };
  }

  private static void play(HiLo hilo) {
    Scanner stdin = new Scanner(System.in); // cannot use System.console with IDEA

    System.out.printf("Playing HiLo between %d and %d.%n", hilo.low, hilo.high);
    
    while (!hilo.solved()) {
      var choices = hilo.choices();
      int min = choices.getMinimum();
      int max = choices.getMaximum();
      var question =
          (max == min) ? "Is your number %d ? ".formatted(min)
          : "Is your number between %d and %d ? ".formatted(min, max);
      System.out.print(question);
      var ans = yesOrNo(stdin.nextLine());
      while (ans.isEmpty()) {
        System.out.print("  yes or no? ");
        ans = yesOrNo(stdin.nextLine());
      }
      if (ans.get())
        hilo.yes();
      else
        hilo.no();
      System.out.printf("I'm %.0f%% done.%n", hilo.progress() * 100.0);
    }
    System.out.printf("Your number is: %d.%n", hilo.secret());
  }

  private static void usage() {
    System.out.println("Usage: HiLo <max>");
  }

  /**
   * Command-line application.
   * <p>
   * Requires a single positive number, which is the upper bound of the range of possible numbers
   * (the lower bound is set to 1).
   */
  public static void main(String[] args) {
    if (args.length != 1) usage();
    else try {
      int max = Integer.parseInt(args[0]);
      if (max >= 1)
        play(HiLo.upto(max));
      else
        usage();
    } catch (NumberFormatException e) {
      usage();
    }
  }
}
