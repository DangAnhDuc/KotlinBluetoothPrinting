package com.example.kotlinbluetoothprinter

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PrintingCallback {

    internal  var printing: Printing?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        if(printing!=null){
            printing!!.printingCallback= this
        }
        btn_pari_unpiar.setOnClickListener{
            if(Printooth.hasPairedPrinter())
                Printooth.removeCurrentPrinter()
            else
            {
                startActivityForResult(Intent(this@MainActivity, ScanningActivity::class.java),
                    ScanningActivity.SCANNING_FOR_PRINTER)
                changePairAndUnpair()
            }
        }
        btn_printImage.setOnClickListener{
            if(!Printooth.hasPairedPrinter())
                startActivityForResult(Intent(this@MainActivity, ScanningActivity::class.java),
                    ScanningActivity.SCANNING_FOR_PRINTER)
            else
                printImage()
        }

        btn_printImage.setOnClickListener{
            if(!Printooth.hasPairedPrinter())
                startActivityForResult(Intent(this@MainActivity, ScanningActivity::class.java),
                    ScanningActivity.SCANNING_FOR_PRINTER)
            else
                printText()
        }
    }

    private fun printText() {
        var printables = ArrayList<Printable>()
        printables.add(RawPrintable.Builder(byteArrayOf(27,100,4)).build())

        //Add text
        printables.add(TextPrintable.Builder()
            .setText("Hello world: sadasddsadas")
            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
            .setNewLinesAfter(1)
            .build())

        //Custom Text
        printables.add(TextPrintable.Builder()
            .setText("Hello world")
            .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
            .setNewLinesAfter(1)
            .build())

        printing!!.print(printables)
    }

    private fun printImage() {
    }

    private fun changePairAndUnpair() {
        if(Printooth.hasPairedPrinter())
            btn_pari_unpiar.text="Unpair ${Printooth.getPairedPrinter()!!.name}"
        else
            btn_pari_unpiar.text="Pair with Printer"
    }

    override fun connectingWithPrinter() {
        Toast.makeText(this,"Connecting to printer",Toast.LENGTH_SHORT).show()

    }

    override fun connectionFailed(error: String) {
        Toast.makeText(this,"Faild: $error",Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: String) {
        Toast.makeText(this,"Error: $error",Toast.LENGTH_SHORT).show()
    }

    override fun onMessage(message: String) {
        Toast.makeText(this,"Message: $message",Toast.LENGTH_SHORT).show()
    }

    override fun printingOrderSentSuccessfully() {
        Toast.makeText(this,"Connecting to printer",Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==ScanningActivity.SCANNING_FOR_PRINTER
            && requestCode==Activity.RESULT_OK)
            initPrinting()
        changePairAndUnpair()
    }

    private fun initPrinting() {
          if(Printooth.hasPairedPrinter())
              printing= Printooth.printer()
          if(printing!=null)
              printing!!.printingCallback=this
    }
}
