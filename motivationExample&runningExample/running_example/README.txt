For the example in Section 3, we introduce the workflow of EXPECT to localize the fault in "Faulty Version".

For Phase-1 (Attaining Pseudo Correct Versions). Different strategies can be used to get pseudo correct versions that reverse the execution results of failed test cases. In "Faulty Version", we have only one failed test case, and this test case passes in "Substitute Version" introduced in Section 4.2. "Substitute Version" can be regarded as a different branch version or a mutation of "Faulty Version", we apply it as the pseudo correct version.

For Phase-2 (Bifurcation Point Tracing). We collect the exception trigger streams while executing the failed test case in both "Faulty Version" and "Substitute Version", the two streams represent the abnormal and normal execution status of the program respectively. The bifurcation point is found in the last checkpoint, as depicted in Section 3.

For Phase-3 (Voting). The bifurcation point is found in Situation-5 according to Section 4.4. Thus, the failed test case votes for statements executed between the bifurcation point and the previous checkpoint. After this phase, 87 statements are voted for by the failed test case, while the faulty statement lies between them. The vote-based ranking is given in this repository.

For Phase-4 (Tie-breaking). To break the tie of 87 statements with the same number of votes, we use Crosstab as the tie-breaking tool. After employing Crosstab, there are 5 statements ranked above the faulty statement, and 1 statement ranked equally.

We also localize the fault in "Faulty Version" based on SBFL techniques for comparison. As a result, SBFL techniques fail to achieve high effectiveness, specifically, Crosstab, Naish2, Jaccard, Tarantula and DStar all rank the faulty statement as the 13th suspicious. Compared with these SBFL techniques, EXPECT achieves an improvement of 54%. This example shows the competitiveness of EXPECT, in fact, EXPECT improves significantly not only in effectiveness but also in tie-breaking, especially when existing techniques fail to deliver fine-grained results.
