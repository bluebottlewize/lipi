package org.bluebottlewize.lipi;

import static org.bluebottlewize.lipi.Alphabets.MAL_VOWEL_E;
import static org.bluebottlewize.lipi.Alphabets.MAL_VOWEL_EE;

public class MalayalamGraphemeGenerator
{


    public static boolean isHalfVowelBefore(String letter)
    {
        return MAL_VOWEL_E.equals(letter) || MAL_VOWEL_EE.equals(letter);
    }
}
