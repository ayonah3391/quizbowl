package com.example.quizbowl;

public class Question {
    String questionText = "";
    String answerText = "";

    public Question(String question) {
        String[] questionArray = question.split("ANSWER:");
        questionText = questionArray[0];
        answerText = questionArray[1];
    }

    // getters and setters
    public String getQuestionText() {
        return questionText;
    }
    public String getAnswerText() {
        return answerText;
    }
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public String clean(String txt) {
        int indOfOpenBracket = txt.indexOf('[');
        int indOfCloseBracket = txt.indexOf(']');
        int indOfOpenParan = txt.indexOf('(');
        int indOfCloseParan = txt.indexOf(')');

        String cleaned = "";

        if(indOfOpenBracket == -1 && indOfOpenParan == -1) {
            cleaned = txt;
        } else if (indOfOpenBracket == -1) {
            if(indOfOpenParan == 0) {
                cleaned = txt.substring(indOfCloseParan+1);
            } else {
                cleaned = txt.substring(0, indOfOpenParan);
            }
        } else if (indOfOpenParan == -1) {
            if(indOfOpenBracket == 0) {
                cleaned = txt.substring(indOfCloseBracket+1);
            } else {
                cleaned = txt.substring(0, indOfOpenBracket);
            }
        } else if (indOfOpenBracket < indOfOpenParan) {
            cleaned = txt.substring(indOfCloseBracket+1 , indOfOpenParan);
        } else if (indOfOpenBracket > indOfOpenParan) {
            cleaned = txt.substring(indOfOpenParan, indOfCloseBracket+1);
        } else if (indOfOpenParan > indOfOpenBracket && indOfOpenParan < indOfCloseBracket) {
            if(indOfOpenBracket == 0) {
                cleaned = txt.substring(indOfCloseBracket+1);
            } else {
                cleaned = txt.substring(0, indOfOpenBracket);
            }
        }

        return cleaned;
    }
    public boolean checkAnswer(String userAnswer) {
        boolean isCorrect = false;
        userAnswer = userAnswer.toLowerCase();
        answerText = answerText.toLowerCase();



        if(userAnswer.equalsIgnoreCase(getAnswerText())){
            isCorrect = true;
        } else if(answerText.indexOf(userAnswer) != -1) {
            isCorrect = true;
        } else {
            double Similarity = 0;
            String s1 = clean(getAnswerText());
            String longer = userAnswer, shorter = s1;
            if (userAnswer.length() < s1.length()) { // longer should always have greater length
                longer = s1; shorter = userAnswer;
            }
            int longerLength = longer.length();

            if (longerLength == 0) {
                Similarity = 1;
            } else {

                Similarity = (longerLength - editDistance(longer, shorter)) / (double) longerLength;
            }

            if(Similarity >= 0.5) {
                isCorrect = true;
            } else {
                isCorrect = false;
            }
        }
        return isCorrect;
    }
}
