#!/usr/bin/env python3

import sys
from typing import Sequence, Optional, List


class HiLo:
    """A Hi-Lo game.

    The computer guesses the user's secret number by asking questions.

    Note that only the methods yes() and no() have side effects. Other methods can be called
    repeatedly and will produce the same values until yes() or no() is called.

    Contrast with the Java and Scala implementations.
    """

    def __init__(self, minimum: int, maximum: int) -> None:
        """Primary constructor.

        min: lower bound of the range of possible secret numbers; cannot negative
        max: upper bound of the range of possible secret numbers; cannot be more than 999999999
        """

        if minimum > maximum:
            raise ValueError("range cannot be empty")
        if minimum < 0:
            raise ValueError("min cannot be negative")
        if maximum >= 1_000_000_000:
            raise ValueError("max must be less than 1000000000")

        self.low = minimum
        self.high = maximum
        self.size = maximum - minimum + 1

    @classmethod
    def upto(cls, maximum: int) -> 'HiLo':
        """Creates an instance of HiLo with range 1..max."""

        return cls(1, maximum)

    def _mid_point(self) -> int:
        return (self.low + self.high) // 2

    def choices(self) -> Sequence[int]:
        """The question to the user, as a range.

        Semantically, the question is: Is your secret number within this range?
        It is illegal to call this method if the problem has already been solved.
        """

        if self.solved():
            raise ValueError("problem has been solved already")
        assert self.low <= self._mid_point()
        return range(self.low, self._mid_point() + 1)

    def solved(self) -> bool:
        """Termination.

        That is, whether the problem has been solved. Note that a problem might be solved without
        any user interaction (if the initial range is reduced to one value).
        """

        return self.low == self.high

    def yes(self) -> None:
        """Yes answer.

        This method is called by the user to indicate that the secret number is present in the
        proposed range. It generates a new question as a side effect, unless the problem is solved.
        It is illegal to call this method if the problem has already been solved.
        """

        if self.solved():
            raise ValueError("problem has been solved already")
        self.high = self._mid_point()
        assert self.low <= self.high

    def no(self) -> None:
        """No answer.

        This method is called by the user to indicate that the secret number is not present in the
        proposed range. It generates a new question as a side effect, unless the problem is solved.
        It is illegal to call this method if the problem has already been solved.
        """

        if self.solved():
            raise ValueError("problem has been solved already")
        self.low = self._mid_point() + 1
        assert self.low <= self.high

    def progress(self) -> float:
        """Progress towards guessing.

        The contract for this method is as follows:

        - progress is exactly 1.0 when a problem is solved;
        - progress is exactly 0.0 initially, unless the problem is solved
          (in which case, it is 1.0);
        - progress increases after each call to yes or no until the problem is solved.

        Good progress methods are based on the logarithm of the size of the range of possible
        numbers (since the number of steps required to solve the problem by dichotomy is in the
        order of the logarithm of the size), but simpler functions (that increase slowly before
        they jump) can be devised that also satisfy the contract.
        """

        from math import log
        return 1.0 if self.solved() else 1.0 - log(self.high - self.low + 1) / log(self.size)

    def secret(self) -> int:
        """The secret number.

        This method can only be called after the problem has been solved.
        """

        if not self.solved():
            raise ValueError("problem not solved yet")
        assert self.low == self.high
        return self.low


_yes = {'y', 'Y', 'yes', 'Yes', 'YES'}
_no = {'n', 'N', 'no', 'No', 'NO'}


def _yes_no(line: str) -> Optional[bool]:
    ans = line.strip()
    return True if ans in _yes else False if ans in _no else None


def _play(hilo: HiLo) -> None:
    print(f"Playing HiLo between {hilo.low} and {hilo.high}.")
    while not hilo.solved():
        choices = hilo.choices()
        if len(choices) == 1:
            question = f"Is your number {choices[0]}? "
        else:
            question = f"Is your number between {choices[0]} and {choices[-1]}? "
        ans = _yes_no(input(question))
        while ans is None:
            ans = _yes_no(input("  yes or no? "))
        hilo.yes() if ans else hilo.no()
        print(f"I'm {hilo.progress() * 100.0:.0f}% done.")
    print(f"Your number is: {hilo.secret()}")


def _usage() -> None:
    print("Usage: HiLo <max>")


def main(argv: List[str]) -> None:
    """Command-line application.

    Requires a single positive number, which is the upper bound of the range of possible numbers
    (the lower bound is set to 1).
    """

    if len(argv) != 2:
        _usage()
    else:
        try:
            _play(HiLo.upto(int(argv[1])))
        except ValueError:
            _usage()


if __name__ == '__main__':
    main(sys.argv)
