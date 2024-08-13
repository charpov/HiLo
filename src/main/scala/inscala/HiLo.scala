package inscala

import org.scalactic.Requirements.{ require, requireState }

/** A Hi-Lo game. The computer guesses the user's secret number by asking questions.
  *
  * Note that only the methods `yes()` and `no()` have side effects. Other methods can be called
  * repeatedly and will produce the same values until `yes()` or `no()` is called.
  *
  * @note
  *   Contrast with the Java implementation.
  *
  * @author
  *   Michel Charpentier
  *
  * @constructor
  *   Primary constructor. See alternative in the companion object.
  *
  * @param min
  *   lower bound (included) of the range of possible secret numbers; cannot be negative.
  *
  * @param max
  *   upper bound (included) of the range of possible secret numbers; cannot be more than
  *   999_999_999.
  *
  * @throws IllegalArgumentException
  *   if the specified range is empty, parameter 'min' is negative, or parameter 'max' is larger
  *   than 999_999_999.
  */
final class HiLo(min: Int, max: Int):
   require(min <= max, "range cannot be empty")
   require(min >= 0, "min cannot be negative")
   require(max < 1_000_000_000, "max must be less than 1_000_000_000")

   private var low  = min
   private var high = max
   private val size = max - min + 1

   private def midpoint = (low + high) / 2

   /** The question to the user, as a range.
     *
     * Semantically, the question is: Is your secret number within this range? It is illegal to call
     * this method if the problem has already been solved.
     *
     * @throws IllegalStateException
     *   if the problem has already been solved.
     */
   def choices: Range =
      requireState(!solved, "problem has been solved already")
      assert(low <= midpoint)
      low to midpoint

   /** Termination.
     *
     * That is, whether the problem has been solved. Note that a problem might be solved without any
     * user interaction (if the initial range is reduced to one value).
     */
   def solved: Boolean = low == high

   /** 'Yes' answer.
     *
     * This method is called by the user to indicate that the secret number is present in the
     * proposed range. It generates a new question as a side effect, unless the problem is solved.
     * It is illegal to call this method if the problem has already been solved.
     *
     * @throws IllegalStateException
     *   if the problem has already been solved.
     */
   def yes(): Unit =
      requireState(!solved, "problem has been solved already")
      high = midpoint
      assert(low <= high)

   /** 'No' answer.
     *
     * This method is called by the user to indicate that the secret number is not present in the
     * proposed range. It generates a new question as a side effect, unless the problem is solved.
     * It is illegal to call this method if the problem has already been solved.
     *
     * @throws IllegalStateException
     *   if the problem has already been solved.
     */
   def no(): Unit =
      requireState(!solved, "problem has been solved already")
      low = midpoint + 1
      assert(low <= high)

   /** Progress towards guessing.
     *
     * The contract for this method is as follows:
     *
     *   - progress is exactly 1.0 when a problem is solved;
     *   - progress is exactly 0.0 initially, unless the problem is solved (in which case, it is
     *     1.0);
     *   - progress increases after each call to `yes` or `no` until the problem is solved.
     *
     * Good progress methods are based on the _logarithm_ of the size of the range of possible
     * numbers (since the number of steps required to solve the problem by dichotomy is in the order
     * of the logarithm of the size), but simpler functions (that increase slowly before they jump)
     * can be devised that also satisfy the contract.
     */
   def progress: Double =
      import scala.math.log
      assert(size > 0)
      if solved then 1.0 else 1 - log(high - low + 1) / log(size)

   /** The secret number.
     *
     * This method can only be called after the problem has been solved.
     *
     * @throws IllegalStateException
     *   if called before the problem has been solved.
     */
   def secret: Int =
      requireState(solved, "problem not solved yet")
      assert(low == high)
      low
end HiLo

/** Companion object of the [[HiLo]] class. */
object HiLo:
   /** Creates an instance of [[HiLo]] with range `1..max`.
     *
     * @throws IllegalArgumentException
     *   if parameter 'max' is less than 1 or greater than 999_999_999.
     */
   def upto(max: Int): HiLo = HiLo(1, max)

   private def play(hilo: HiLo) =
      def yesOrNo(line: String): Option[Boolean] =
         line.trim match
            case "y" | "Y" | "yes" | "Yes" | "YES" => Some(true)
            case "n" | "N" | "no" | "No" | "NO"    => Some(false)
            case _                                 => None

      @scala.annotation.tailrec
      def getReply(question: String): Boolean =
         yesOrNo(scala.io.StdIn.readLine(question)) match
            case Some(ans) => ans
            case None      => getReply("  yes or no? ")

      println(s"Playing HiLo between ${hilo.low} and ${hilo.high}.")

      while !hilo.solved do
         val choices = hilo.choices
         val question =
            if choices.sizeIs == 1 then s"Is your number ${choices.head} ? "
            else s"Is your number between ${choices.head} and ${choices.last} ? "
         if getReply(question) then hilo.yes() else hilo.no()
         println(f"I'm ${hilo.progress * 100.0}%.0f%% done.")

      println(s"Your number is: ${hilo.secret}.")
   end play

   /** Command-line application.
     *
     * Requires a single positive number, which is the upper bound of the range of possible numbers
     * (the lower bound is set to 1).
     */
   def main(args: Array[String]): Unit =
      def usage() = println("Usage: HiLo <max>")

      if args.length != 1 then usage()
      else
         args(0).toIntOption match
            case Some(max) if max >= 1 => play(HiLo.upto(max))
            case _                     => usage()
   end main
end HiLo
