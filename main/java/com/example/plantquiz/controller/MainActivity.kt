package com.example.plantquiz.controller

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.plantquiz.R
import com.example.plantquiz.model.DownloadingObjects
import com.example.plantquiz.model.ParsePlantUtility
import com.example.plantquiz.model.Plant
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var cameraButton: Button? = null
    private var photoGalleryButton: Button? = null



    val OPEN_CAMERA_BUTTON_REQUEST_ID = 1000
    val OPEN_PHOTO_GALLERY_BUTTON_REQUEST_ID = 2000

    //Instance Variables
    var correctAnswerIndex: Int = 0
    var correctPlant : Plant? = null

    var numberOfTimeUserAnsweredCorrectly: Int = 0
    var numberOfTimeUserAnsweredInCorrectly: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))


        setProgressBar(false)
        displayUIWidgets(false)

        YoYo.with(Techniques.Pulse)
            .duration(700)
            .repeat(5)
            .playOn(btnNextPlant)


        /*Toast.makeText(this,"THE OnCreate METHOD IS CALLED", Toast.LENGTH_SHORT).show()

        val myPlant: Plant = Plant("","","","","",
                                    "",0,0)
        myPlant.plantName = "Wadas Memory Magnolia"
        var nameOfPlant = myPlant.plantName*/

        var flower = Plant()
        var tree = Plant()

        var collectionOfPlants: ArrayList<Plant> = ArrayList()
        collectionOfPlants.add(flower)
        collectionOfPlants.add(tree)

        cameraButton = findViewById<Button>(R.id.btnOpenCamera)
        photoGalleryButton = findViewById<Button>(R.id.btnOpenPhotoGallery)


        cameraButton?.setOnClickListener(View.OnClickListener {

            Toast.makeText(this, "THE OPEN CAMERA BUTTON IS CLICKED ", Toast.LENGTH_SHORT).show()

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, OPEN_CAMERA_BUTTON_REQUEST_ID)
        })

        photoGalleryButton?.setOnClickListener(View.OnClickListener {

            Toast.makeText(this, "THE OpenPhotoGallery BUTTON IS CLICKED", Toast.LENGTH_SHORT).show()

            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, OPEN_PHOTO_GALLERY_BUTTON_REQUEST_ID)
        })



        // See the next plant
        btnNextPlant.setOnClickListener(View.OnClickListener {

            if (checkForInternetConnection()) {
                setProgressBar(true)

                try {

                    val innerClassObject = DownloadingPlantTask()
                    innerClassObject.execute()

                } catch (e: Exception) {
                    e.printStackTrace()
                }


                var gradientColors: IntArray = IntArray(2)
                gradientColors.set(0, Color.parseColor("#FFEB3B"))
                gradientColors.set(1, Color.parseColor("#FF0000"))
                var gradientDrawable: GradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                        gradientColors)
                var convertDipValue = dipToFloat(this@MainActivity, 50f)
                gradientDrawable.cornerRadius = convertDipValue
                gradientDrawable.setStroke(5, Color.parseColor("#FBFAFA"))

                button1?.setBackground(gradientDrawable)
                button2?.setBackground(gradientDrawable)
                button3?.setBackground(gradientDrawable)
                button4?.setBackground(gradientDrawable)


            }
        })

    }

    fun dipToFloat(context: Context, dipValue: Float): Float {
        val metrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == OPEN_CAMERA_BUTTON_REQUEST_ID) {

            if (resultCode == RESULT_OK) {

                val imageData = data?.getExtras()?.get("data") as Bitmap
                imgTaken.setImageBitmap(imageData)
            }
        }

        if (requestCode == OPEN_PHOTO_GALLERY_BUTTON_REQUEST_ID) {

            if (resultCode == RESULT_OK ) {

                val contentURI = data?.getData()

                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                imgTaken.setImageBitmap(bitmap)

            }
        }
    }

    fun button1IsClicked(buttonView: View) {
//      Toast.makeText(this,"The Button 1 is clicked.", Toast.LENGTH_SHORT).show()
//
//        var myNumber = 8 // Implied Data Type
//        val myName: String = "Omid"
//        var numberOfLetters = myName.length
//
//        var myAnimal: String? = null
//        var numberOfCharactersOfAnimalname = myAnimal?.length ?: 0
        specifyTheRightAndWrongAnswer(0)

    }

    fun button2IsClicked(buttonView: View) {
      //  Toast.makeText(this, "The Button 2 is clicked.", Toast.LENGTH_SHORT).show()
        specifyTheRightAndWrongAnswer(1)
    }

    fun button3IsClicked(buttonView: View) {
        // Toast.makeText(this, "The Button 3 is clicked.", Toast.LENGTH_SHORT).show()
        specifyTheRightAndWrongAnswer(2)
    }

    fun button4IsClicked(buttonView: View) {
        //Toast.makeText(this, "The Button 4 is clicked.", Toast.LENGTH_SHORT).show()
        specifyTheRightAndWrongAnswer(3)
    }



    inner class DownloadingPlantTask: AsyncTask<String, Int, List<Plant>>() {

        override fun doInBackground(vararg params: String?): List<Plant>? {
            //Can access background thread, not user interface thread.

//            val downloadingObject: DownloadingObjects = DownloadingObjects()
//            var jsonData = downloadingObject.downloadJSONDataFromLink(
//                    "http://plantplaces.com/perl/mobile/flashcard.pl")
//
//            Log.i("JSON" , jsonData)
            val parsPlant = ParsePlantUtility()


            return parsPlant.parsePlantObjectsFromJSONData()

        }

        override fun onPostExecute(result: List<Plant>?) {
            super.onPostExecute(result)
            //can access user interface thread, but not background thread.

            var numberOfPlants = result?.size ?: 0
            if(numberOfPlants > 0) {

                var randomPlantIndexForButton1 = (Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton2 = (Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton3 = (Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton4 = (Math.random() * result!!.size).toInt()

                var allRandomPlants = ArrayList<Plant>()
                allRandomPlants.add(result.get(randomPlantIndexForButton1))
                allRandomPlants.add(result.get(randomPlantIndexForButton2))
                allRandomPlants.add(result.get(randomPlantIndexForButton3))
                allRandomPlants.add(result.get(randomPlantIndexForButton4))

                button1?.text = result.get(randomPlantIndexForButton1).toString()
                button2?.text = result.get(randomPlantIndexForButton2).toString()
                button3?.text = result.get(randomPlantIndexForButton3).toString()
                button4?.text = result.get(randomPlantIndexForButton4).toString()

                correctAnswerIndex = (Math.random() * allRandomPlants.size).toInt()
                correctPlant = allRandomPlants.get(correctAnswerIndex)

                val downloadingImageTask = DownloadingImageTask()
                downloadingImageTask.execute(allRandomPlants.get(correctAnswerIndex).picture_name)

            }
        }
    }

    override fun onStart() {
        super.onStart()
        //Toast.makeText(this,"THE OnStart METHOD IS CALLED", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        //Toast.makeText(this,"THE OnResume METHOD IS CALLED", Toast.LENGTH_SHORT).show()
        checkForInternetConnection()

    }

    override fun onPause() {
        super.onPause()
        //Toast.makeText(this,"THE OnPause METHOD IS CALLED", Toast.LENGTH_SHORT).show()

    }

    override fun onStop() {
        super.onStop()
        //Toast.makeText(this,"THE OnStop METHOD IS CALLED", Toast.LENGTH_SHORT).show()

    }

    override fun onRestart() {
        super.onRestart()
        //Toast.makeText(this,"THE OnRestart METHOD IS CALLED", Toast.LENGTH_SHORT).show()

    }

    override fun onDestroy() {
        super.onDestroy()
        //Toast.makeText(this,"THE OnDestroy METHOD IS CALLED", Toast.LENGTH_SHORT).show()

    }

    fun imageViewIsClicked(view: View) {
        /* val randomNumber: Int = (Math.random() * 6).toInt() + 1
        Log.i("TAG","The created number is: $randomNumber")

        if (randomNumber == 1) {
            cameraButton?.setBackgroundColor(Color.YELLOW)

        }else if (randomNumber == 2) {
            photoGalleryButton?.setBackgroundColor(Color.MAGENTA)

        }else if (randomNumber == 3) {
            button1?.setBackgroundColor(Color.WHITE)

        }else if (randomNumber == 4) {
            button2?.setBackgroundColor(Color.GREEN)

        }else if (randomNumber == 5) {
            button3?.setBackgroundColor(Color.RED)

        }else if (randomNumber == 6) {
            button4?.setBackgroundColor(Color.BLUE)
       }


*/
        val randomNumber: Int = (Math.random() * 6).toInt() + 1
        Log.i("TAG", "The created number is $randomNumber")

        when(randomNumber) {

            1 -> cameraButton?.setBackgroundColor(Color.YELLOW)
            2 -> photoGalleryButton?.setBackgroundColor(Color.MAGENTA)
            3 -> button1.setBackgroundColor(Color.WHITE)
            4 -> button2.setBackgroundColor(Color.GREEN)
            5 -> button3.setBackgroundColor(Color.RED)
            6 -> button4.setBackgroundColor(Color.BLUE)
        }
    }
    //Check for internet connectivity
    private fun checkForInternetConnection() : Boolean {
        val connectivityManager: ConnectivityManager = this.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = connectivityManager.activeNetworkInfo
        val isDeviceConnectedToInternet = networkInfo != null
                                                  && networkInfo.isConnectedOrConnecting
        if (isDeviceConnectedToInternet) {

            return true
        }else {

            createAlert()
            return false
        }

    }
    private fun createAlert() {
        var alertDialog : AlertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle("Network Error")
        alertDialog.setMessage("Check for internet connection")

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { dialog: DialogInterface?, which: Int ->
            startActivity(Intent(Settings.ACTION_SETTINGS))

        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel")
        { dialog: DialogInterface?, which: Int ->
            Toast.makeText(this@MainActivity,
                    "You must be connected to the Internet", Toast.LENGTH_SHORT).show()
            finish()
        }
        alertDialog.show()
    }

    private fun specifyTheRightAndWrongAnswer(userGuess: Int) {

        when(correctAnswerIndex) {
            0 -> button1.setBackgroundColor(Color.GREEN)
            1 -> button2.setBackgroundColor(Color.GREEN)
            2 -> button3.setBackgroundColor(Color.GREEN)
            4 -> button4.setBackgroundColor(Color.GREEN)
        }

        if (userGuess == correctAnswerIndex) {

            txtState.setText("Right!")
            numberOfTimeUserAnsweredCorrectly++
            txtRightAnswers.setText("$numberOfTimeUserAnsweredCorrectly")


        }else{
            var correctPlantName = correctPlant.toString()
            txtState.setText("Wrong! Choose: $correctPlantName.")
            numberOfTimeUserAnsweredInCorrectly++
            txtWrongAnswers.setText("$numberOfTimeUserAnsweredInCorrectly")

        }
    }
    inner class DownloadingImageTask: AsyncTask<String, Int, Bitmap?>() {
        override fun doInBackground(vararg pictureName: String?): Bitmap? {

            try {

                val downloadingObject = DownloadingObjects()
                val plantBitmap = downloadingObject.downloadPlantPicture(pictureName[0])

                return plantBitmap

            }catch (e: Exception) {

                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)

            setProgressBar(false)
            displayUIWidgets(true)

            playAnimationOnView(imgTaken, Techniques.Tada)
            playAnimationOnView(button1, Techniques.RollIn)
            playAnimationOnView(button2, Techniques.RollIn)
            playAnimationOnView(button3, Techniques.RollIn)
            playAnimationOnView(button4, Techniques.RollIn)
            playAnimationOnView(txtState, Techniques.Swing)
            playAnimationOnView(txtWrongAnswers, Techniques.FlipInX)
            playAnimationOnView(txtRightAnswers, Techniques.Landing)



            imgTaken.setImageBitmap(result)





        }
    }
    //ProgressBar Visibility
    private fun setProgressBar(show: Boolean) {
        if(show) {
            linearLayoutProgBar.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE    //To show ProgressBar
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        } else if(!show) {
            linearLayoutProgBar.visibility  = View.GONE
            progressBar.visibility = View.GONE    // To hide ProgressBar
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        }
    }

    //Set visibility of ui widgets
    private fun displayUIWidgets(display: Boolean) {

        if(display) {

            imgTaken.visibility = View.VISIBLE
            button1.visibility = View.VISIBLE
            button2.visibility = View.VISIBLE
            button3.visibility = View.VISIBLE
            button4.visibility = View.VISIBLE
            txtState.visibility = View.VISIBLE
            txtWrongAnswers.visibility = View.VISIBLE
            txtRightAnswers.visibility = View.VISIBLE


        }else if (!display) {

            imgTaken.visibility = View.INVISIBLE
            button1.visibility = View.INVISIBLE
            button2.visibility = View.INVISIBLE
            button3.visibility = View.INVISIBLE
            button4.visibility = View.INVISIBLE
            txtState.visibility = View.INVISIBLE
            txtWrongAnswers.visibility = View.INVISIBLE
            txtRightAnswers.visibility = View.INVISIBLE
        }
    }

    //Playing Animations
   private fun playAnimationOnView(view: View? , technique: Techniques) {

       YoYo.with(technique)
           .duration(700)
           .repeat(0)
           .playOn(view)
   }

}