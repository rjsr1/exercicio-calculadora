package br.ufpe.cin.if710.calculadora

import android.app.Activity
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity(), OnClickListener{




    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("resultado",this.displayResult.text.toString())
    }

    override fun onClick(v: View?) {
        try{
            when(v?.id){


                R.id.btn_Equal->{
                    var resultado=eval(Expressao)
                    displayResult.setText(resultado.toString())
                    Expressao="";
                }
                R.id.btn_Clear->{
                    displayResult.setText("");
                    Expressao="";
                }
                else->{
                    var button=v as Button
                    Expressao=Expressao+button.text
                    displayResult.setText(Expressao)
                }
            }
        }catch (e:Exception){
           Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show();
        }




    }

    var Expressao:String=""
    val Botoes:ArrayList<Button> = arrayListOf();
    lateinit var displayResult:EditText;

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        displayResult= findViewById<EditText>(R.id.text_calc)
        displayResult.setText(savedInstanceState?.getString("resultado"))

        Expressao = displayResult.text.toString();

        Botoes.add(findViewById(R.id.btn_0));
        Botoes.add(findViewById(R.id.btn_1));
        Botoes.add(findViewById(R.id.btn_2));
        Botoes.add(findViewById(R.id.btn_3));
        Botoes.add(findViewById(R.id.btn_4));
        Botoes.add(findViewById(R.id.btn_5));
        Botoes.add(findViewById(R.id.btn_6));
        Botoes.add(findViewById(R.id.btn_7));
        Botoes.add(findViewById(R.id.btn_8));
        Botoes.add(findViewById(R.id.btn_9));
        Botoes.add(findViewById(R.id.btn_Divide));
        Botoes.add(findViewById(R.id.btn_Multiply));
        Botoes.add(findViewById(R.id.btn_Subtract));
        Botoes.add(findViewById(R.id.btn_Add));
        Botoes.add(findViewById(R.id.btn_Equal));
        Botoes.add(findViewById(R.id.btn_Dot));
        Botoes.add(findViewById(R.id.btn_Power));
        Botoes.add(findViewById(R.id.btn_Clear));
        Botoes.add(findViewById(R.id.btn_LParen));
        Botoes.add(findViewById(R.id.btn_RParen));

        Botoes.forEach{
            it.setOnClickListener(this);
        }



    }

    //Como usar a função:
    // eval("2+2") == 4.0
    // eval("2+3*4") = 14.0
    // eval("(2+3)*4") = 20.0
    //Fonte: https://stackoverflow.com/a/26227947
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '
            fun nextChar() {
                val size = str.length
                ch = if ((++pos < size)) str.get(pos) else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Caractere inesperado: " + ch)
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            // | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'))
                        x += parseTerm() // adição
                    else if (eat('-'))
                        x -= parseTerm() // subtração
                    else
                        return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'))
                        x *= parseFactor() // multiplicação
                    else if (eat('/'))
                        x /= parseFactor() // divisão
                    else
                        return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // + unário
                if (eat('-')) return -parseFactor() // - unário
                var x: Double
                val startPos = this.pos
                if (eat('(')) { // parênteses
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') { // números
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                } else if (ch in 'a'..'z') { // funções
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    if (func == "sqrt")
                        x = Math.sqrt(x)
                    else if (func == "sin")
                        x = Math.sin(Math.toRadians(x))
                    else if (func == "cos")
                        x = Math.cos(Math.toRadians(x))
                    else if (func == "tan")
                        x = Math.tan(Math.toRadians(x))
                    else
                        throw RuntimeException("Função desconhecida: " + func)
                } else {
                    throw RuntimeException("Caractere inesperado: " + ch.toChar())
                }
                if (eat('^')) x = Math.pow(x, parseFactor()) // potência
                return x
            }
        }.parse()
    }
}
