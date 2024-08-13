/* This is an example of type classes. All the tests are written in terms of a HiLo type class.
 * They can be run on both the Scala and Java implementation by providing evidence that these
 * implementations belong to the class (ScalaHiLoIsHiLo and JavaHiLoIsHiLo). Note how the Java
 * implementation uses a different Range type and needs to be slightly adapted. No more adaptation
 * is needed here because both implementations already use the same method names and signatures,
 * but in general, type classes can bridge bigger gaps if needed. Note also the use of a create
 * function that plays the role of a constructor (something that cannot be done as easily with
 * only subtype polymorphism).
 */

// The type class itself
trait HiLo[H]:
   def create(min: Int, max: Int): H
   def yes(hilo: H): Unit
   def no(hilo: H): Unit
   def solved(hilo: H): Boolean
   def secret(hilo: H): Int
   def choices(hilo: H): Range
   def progress(hilo: H): Double

// Convenience to retrieve implicit arguments
object HiLo:
   def apply[H: HiLo]: HiLo[H] = summon

// Make the type class functions available as methods
extension [H: HiLo](hilo: H)
   def yes(): Unit      = HiLo[H].yes(hilo)
   def no(): Unit       = HiLo[H].no(hilo)
   def solved: Boolean  = HiLo[H].solved(hilo)
   def secret: Int      = HiLo[H].secret(hilo)
   def choices: Range   = HiLo[H].choices(hilo)
   def progress: Double = HiLo[H].progress(hilo)

// Provide evidence that the Scala implementation belongs to the class
given ScalaHiLoIsHiLo: HiLo[inscala.HiLo] with
   import inscala.HiLo as H
   def create(min: Int, max: Int) = H(min, max)
   def yes(hilo: H)               = hilo.yes()
   def no(hilo: H)                = hilo.no()
   def solved(hilo: H)            = hilo.solved
   def secret(hiLo: H)            = hiLo.secret
   def choices(hilo: H)           = hilo.choices
   def progress(hilo: H)          = hilo.progress

// Provide evidence that the Java implementation belongs to the class
given JavaHiLoIsHiLo: HiLo[injava.HiLo] with
   import injava.HiLo as H
   def create(min: Int, max: Int) = H(min, max)
   def yes(hilo: H)               = hilo.yes()
   def no(hilo: H)                = hilo.no()
   def solved(hilo: H)            = hilo.solved
   def secret(hiLo: H)            = hiLo.secret
   def progress(hilo: H)          = hilo.progress
   def choices(hilo: H) = // adapt the Apache Range type into a Scala Range
      val range = hilo.choices
      Range.inclusive(range.getMinimum, range.getMaximum)
