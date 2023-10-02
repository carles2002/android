import android.os.AsyncTask
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class JsonRequestTask : AsyncTask<Pair<String, String>, Void, String>() {

    override fun doInBackground(vararg params: Pair<String, String>): String? {
        var connection: HttpURLConnection? = null
        var reader: BufferedReader? = null
        var result: String? = null

        try {
            val url = URL(params[0].first) // URL de tu servidor PHP
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val outputStream: OutputStream = connection.outputStream
            val writer = OutputStreamWriter(outputStream, "UTF-8")
            writer.write(params[0].second) // Envía el JSON como una cadena
            writer.close()
            outputStream.close()

            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream: InputStream = connection.inputStream
                reader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
                result = stringBuilder.toString()
            } else {
                // Manejar el error de la solicitud aquí
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
            try {
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }

    override fun onPostExecute(result: String?) {
        // Procesa la respuesta del servidor aquí
        if (result != null) {
            // La variable 'result' contiene la respuesta del servidor
        }
    }
}
