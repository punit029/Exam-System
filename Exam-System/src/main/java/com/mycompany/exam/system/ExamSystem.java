
package com.mycompany.exam.system;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.Timer;

class Question {
    private String text;
    private String[] options;
    private int answer;

    public Question(String text, String[] options, int answer) {
        this.text = text;
        this.options = options;
        this.answer = answer;
    }

    public String getText() {
        return text;
    }

    public String[] getOptions() {
        return options;
    }

    public int getAnswer() {
        return answer;
    }
}
class ExamSystem extends JFrame implements ActionListener
{
JLabel l;
JRadioButton jb[]=new JRadioButton[5];
JButton b1,b2;
ButtonGroup bg;
int count=0,current=0,x=1,y=1,now=0;
int m[]=new int[10];
ArrayList<Question> questions;
 Timer timer;
    int timeLeft = 60;
ExamSystem(String s)
{
super(s);
questions = new ArrayList<>();

l=new JLabel();
add(l);
bg=new ButtonGroup();
for(int i=0;i<5;i++)
{
jb[i]=new JRadioButton();
add(jb[i]);
bg.add(jb[i]);
}
b1=new JButton("Next");
b2=new JButton("Bookmark");
b1.addActionListener(this);
b2.addActionListener(this);
add(b1);add(b2);

l.setBounds(30,40,450,20);
jb[0].setBounds(50,80,100,20);
jb[1].setBounds(50,110,100,20);
jb[2].setBounds(50,140,100,20);
jb[3].setBounds(50,170,100,20);
b1.setBounds(100,240,100,30);
b2.setBounds(270,240,100,30);
setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
setLayout(null);
setLocation(250,100);
setVisible(true);
setSize(600,350);

 timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                setTitle("Online Test - Time left: " + timeLeft + " seconds");
                if (timeLeft == 0) {
                    timer.stop();
                    evaluateTest();
                }
            }
        });
        timer.start();
 
    try {
        
        Class.forName("com.mysql.cj.jdbc.Driver");
     
       Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase7","root","12345678");
        
        String sql = "SELECT question_text, answer_options, correct_answer_index FROM questions";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            String text = rs.getString("question_text");
            String[] options = rs.getString("answer_options").split(",");
            int answer = rs.getInt("correct_answer_index");
            questions.add(new Question(text, options, answer));
        }
        rs.close();
        stmt.close();
        con.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    set();
}
 public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) {
            if (check()) {
                count++;
            }
            current++;
            set();
            if (current == 9) {
                b1.setEnabled(false);
                b2.setText("Result");
            }
        } else if (e.getSource() == b2) {
            if (b2.getText().equals("Result")) {
                evaluateTest();
                return;
            }
            JButton bk = new JButton("Bookmark" + x);
            bk.setBounds(480, 20 + 30 * x, 100, 30);
            add(bk);
            bk.addActionListener(this);
            m[x] = current;
            x++;
            current++;
            set();
            if (current == 9) {
                b2.setText("Result");
            }
            setVisible(false);
            setVisible(true);
        } else {
            for (int i = 0, y = 1; i < x; i++, y++) {
                if (e.getActionCommand().equals("Bookmark" + y)) {
                    if (check()) {
                        count++;
                    }
                    now = current;
                    current = m[y];
                    set();
                    ((JButton) e.getSource()).setEnabled(false);
                    current = now;
                }
            }
        }
 }
void set() {
    Question question = questions.get(current);
    l.setText(question.getText());
    String[] options = question.getOptions();
    for (int i = 0; i < 4; i++) {
        jb[i].setText(options[i]);
    }
    jb[4].setSelected(true);
}
boolean check() {
    String selected = null;
    for (int i = 0; i < 4; i++) {
        if (jb[i].isSelected()) {
            selected = jb[i].getText();
        }
    }
    if (selected == null) {
        return false;
    }
    Question question = questions.get(current);
    return selected.equals(question.getOptions()[question.getAnswer()]);
}
public static void main(String s[])
{
    GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
 GraphicsDevice device = graphics.getDefaultScreenDevice();
device.setFullScreenWindow(new ExamSystem("Online Test Of Java"));
}

    private void evaluateTest() {
        JOptionPane.showMessageDialog(this,"correct ans="+count);
System.exit(0);
    }
}
