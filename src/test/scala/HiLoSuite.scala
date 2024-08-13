import org.scalatest.Suites
import org.scalatest.funsuite.AnyFunSuite

class AllTests
    extends Suites(
      HiLoSuite[inscala.HiLo]("Scala"),
      HiLoSuite[injava.HiLo]("Java"),
    )

class HiLoSuite[H: HiLo](name: String) extends AnyFunSuite:
   override def suiteName = super.suiteName + s" ($name)"

   private def create(min: Int, max: Int): H = HiLo[H].create(min, max)
   private def upto(max: Int): H             = create(1, max)

   test("Empty ranges are invalid"):
      assertThrows[IllegalArgumentException](create(5, 4))

   test("Negative bounds are invalid"):
      assertThrows[IllegalArgumentException](create(-5, 5))

   test("Bounds larger than 999999999 are invalid"):
      assertThrows[IllegalArgumentException](create(1000, 1_000_000_000))

   test("Singleton ranges are valid"):
      create(3, 3)
      create(1, 1)

   test("Non singleton ranges are not immediately solved"):
      assert(!create(3, 6).solved)

   test("Singleton ranges are immediately solved"):
      assert(create(3, 3).solved)

   test("Singleton ranges are correctly solved"):
      assert(create(3, 3).secret == 3)

   test("'choices' cannot be called after solved"):
      val hilo = upto(761)
      while !hilo.solved do hilo.yes()
      assertThrows[IllegalStateException](hilo.choices)

   test("'secret' cannot be called until solved (1)"):
      val hilo = upto(761)
      assert(!hilo.solved)
      assertThrows[IllegalStateException](hilo.secret)

   test("'secret' cannot be called until solved (2)"):
      val hilo = upto(761)
      if hilo.choices.size > 1 then hilo.yes() else hilo.no()
      assert(!hilo.solved)
      assertThrows[IllegalStateException](hilo.secret)

   test("Cannot call 'yes' after solved"):
      val hilo = upto(761)
      while !hilo.solved do hilo.yes()
      assertThrows[IllegalStateException](hilo.yes())

   test("Cannot call 'no' after solved"):
      val hilo = upto(761)
      while !hilo.solved do hilo.no()
      assertThrows[IllegalStateException](hilo.no())

   test("'choices' can be called multiple times"):
      import org.scalactic.TimesOnInt.*
      val hilo = upto(761)
      while !hilo.solved do
         100 times hilo.choices
         hilo.yes()

   test("'progress' is 0.0 initially (unless solved)"):
      assert(upto(761).progress == 0.0)

   test("'progress' is 1.0 initially on singletons"):
      assert(create(761, 761).progress == 1.0)

   test("'progress' is 1.0 after solved (1)"):
      val hilo = upto(761)
      while !hilo.solved do hilo.yes()
      assert(hilo.progress == 1.0)

   test("'progress' is 1.0 after solved (2)")
   val hilo = upto(761)
   while !hilo.solved do hilo.no()
   assert(hilo.progress == 1.0)

   test("'progress' increases (1)"):
      val hilo = upto(1000)
      var p    = 0.0
      while !hilo.solved do
         hilo.yes()
         val q = hilo.progress
         assert(q > p)
         p = q

   test("'progress' increases (2)"):
      val hilo = upto(1000)
      var p    = 0.0
      while !hilo.solved do
         hilo.no()
         val q = hilo.progress
         assert(q > p)
         p = q

   test("'progress' increases (3)"):
      val hilo = upto(1000)
      var p    = 0.0
      var y    = true
      while !hilo.solved do
         if y then hilo.yes() else hilo.no()
         y = !y
         val q = hilo.progress
         assert(q > p)
         p = q

   test("Solved instance (always yes)"):
      val hilo = upto(1000)
      while !hilo.solved do hilo.yes()

   test("Solved instance (always no)"):
      val hilo = upto(1000)
      while !hilo.solved do hilo.no()

   val problems = Seq(
     (1, 10, 5),
     (1, 100, 42),
     (1, 1000, 421),
     (1, 10_000, 1),
     (1, 10_000, 10_000),
     (1, 10_000, 2017),
     (1, 1_000_000, 54321),
     (0, 999_999_999, 54321),
     (11, 12, 11),
     (11, 12, 12)
   )

   for (min, max, target) <- problems do
      test(s"playing $min..$max, secret is $target"):
         val hilo = create(min, max)
         while !hilo.solved do if hilo.choices.contains(target) then hilo.yes() else hilo.no()
         assert(hilo.secret == target)
