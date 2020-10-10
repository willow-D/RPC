public class Test {

    public static void main(String[] args) {

        String s = "babad";
        String ans = longestPalindrome(s);
        System.out.println(ans);


    }
    public  static String longestPalindrome(String s) {
        if(s.length()<2){
            return s;
        }
        //填充字符
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("#");
        for(int i=0;i<s.length();i++){
            stringBuilder.append(s.charAt(i));
            stringBuilder.append("#");
        }
        s = stringBuilder.toString();

        int center = 0;
        int maxRigth = 0;
        int res = 0;
        int[] palindrome = new int[s.length()];
        for(int i=0;i<s.length();i++){
            int mrrior = 2*center - i;
            if(i>=maxRigth || (i+palindrome[mrrior])>=maxRigth){
                maxRigth = expendMaxRight(s, i);
                center = i;
                palindrome[i] = maxRigth - i + 1;
            }
            else{
                palindrome[i] = palindrome[mrrior];
            }
            res = Math.max(res, palindrome[i]);
        }

        for(int i=0;i<s.length();i++){
            if(palindrome[i]==res){
                s = s.substring(i-res+1, i+res+1);
                s = s.replace("#", "");
                break;
            }
        }
        return s;

    }

    public static int expendMaxRight(String s, int i){
        int left = i-1;
        int right = i+1;
        while(left>=0 && right<s.length() && s.charAt(left)==s.charAt(right)){
            left--;
            right++;
        }
        return right-1;
    }
}
