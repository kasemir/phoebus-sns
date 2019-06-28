package Eliza;
/** Eliza v0.1 written by Charles Hayden chayden@monmouth.com
 *
 *  From http://chayden.net/eliza/Eliza.html by Charles Hayden
 *
 *  .. implements the classic "Eliza" program, a program that communicates in natural language.
 *  It pretends to be a Rogerian psychologist.
 *
 *  The original ELIZA was described by Joseph Weizenbaum in Communications of the ACM in January 1966.
 *  ELIZA was one of the first programs that attempted to communicate in natural language. The article
 *  was an attempt to demystify the behavior of the program, and included a detailed description of the
 *  program. ELIZA is based on a "script" consisting of patterns and corresponding responses.
 *  An appendiz to the article contained the complete script for the Rogerial psychologist.
 *
 *  This rendition of ELIZA is a complete and faithful implementation of the program described by Weizenbaum.
 *  The script language has been reformatted for clarity, but is unchanged in its content.
 *
 *
 *  How Eliza Works
 *
 *  All the behavior of Eliza is controlled by a script file.
 *  The standard script is attached to the end of this explanation.
 *
 *  Eliza starts by reading the script file.  Because of Java security, it
 *  must be on the same server as the class files.  Eliza then reads a line at
 *  a time from the user, processes it, and formulates a reply.
 *
 *  Processing consists of the following steps.
 *  First the sentence broken down into words, separated by spaces.  All further
 *  processing takes place on these words as a whole, not on the individual
 *  characters in them.
 *  Second, a set of pre-substitutions takes place.
 *  Third, Eliza takes all the words in the sentence and makes a list of all
 *  keywords it finds.  It sorts this keyword list in descending weight.  It
 *  process these keywords until it produces an output.
 *  Fourth, for the given keyword, a list of decomposition patterns is searched.
 *  The first one that matches is selected.  If no match is found, the next keyword
 *  is selected instead.
 *  Fifth, for the matching decomposition pattern, a reassembly pattern is
 *  selected.  There may be several reassembly patterns, but only one is used
 *  for a given sentence.  If a subsequent sentence selects the same decomposition
 *  pattern, the next reassembly pattern in sequence is used, until they have all
 *  been used, at which point Eliza starts over with the first reassembly pattern.
 *  Sixth, a set of post-substitutions takes place.
 *  Finally, the resulting sentence is displayed as output.
 *
 *  The script is used to construct the pre and post substitution lists, the
 *  keyword lists, and the decomposition and reassembly patterns.
 *  In addition, there is a synonym matching facility, which is explained below.
 *
 *  Every line of script is prefaced by a tag that tells what list it is
 *  part of.  Here is an explanation of the tags.
 *
 *  initial:    Eliza says this when it starts.
 *  final:      Eliza says this when it quits.
 *  quit:       If the input is this, then Eliza quits.  Any number permitted.
 *  pre:        Part of the pre-substitution list.  If the first word appears in
 *              the sentence, it is replaced by the rest of the words.
 *  post:       Part of the post-subsititution list.  If the first word appears
 *              in the sentence, it is replaced by the rest of the words.
 *  key:        A keyword.  Keywords with greater weight are selected in
 *              preference to ones with lesser weight.
 *              If no weight is given, it is assumed to be 1.
 *  decomp:     A decomposition pattern.  The character * stands for any
 *              sequence of words.
 *  reasmb:     A reassembly pattern.  A set of words matched by * in
 *              the decomposition pattern can be used as part of the reassembly.
 *              For instance, (2) inserts the words matched by the second *
 *              in the decomposition pattern.
 *  synon:      A list of synonyms.  In a decomposition rule, for instance, @be
 *              matches any of the words "be am is are was" because of the line:
 *              "synon: be am is are was".  The match @be also counts as a *
 *              in numbering the matches for use by reassembly rules.
 *
 *  Other Special Rules
 *  If a $ appears first in a decomposition rule, then the output is formed as
 *  normal, but is saved and Eliza goes on to the next keyword.  If no keywords
 *  match, and there are saved sentences, one of them is picked at random and
 *  used as the output, then it is discarded.
 *  If there are no saved sentences, and no keywords match, then it uses the
 *  keyword "xnone".
 */
