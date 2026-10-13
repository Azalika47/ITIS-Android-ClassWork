package com.example.klassw1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val animals = listOf(
            "Лев;5 лет;Царь зверей, обитает в саваннах Африки.;https://ru.wikipedia.org/wiki/%D0%9B%D0%B5%D0%B2",
            "Тигр;4 года;Крупнейшая кошка на Земле с уникальными полосками.;https://ru.wikipedia.org/wiki/%D0%A2%D0%B8%D0%B3%D1%80",
            "Панда;3 года;Бамбуковый медведь из Китая, любит поспать.;https://ru.wikipedia.org/wiki/%D0%91%D0%BE%D0%BB%D1%8C%D1%88%D0%B0%D1%8F_%D0%BF%D0%B0%D0%BD%D0%B4%D0%B0",
            "Жираф;7 лет;Самое высокое животное, спит всего по 20 минут в день.;https://ru.wikipedia.org/wiki/%D0%96%D0%B8%D1%80%D0%B0%D1%84",
            "Слон;10 лет;Огромное и умное животное с потрясающей памятью.;https://ru.wikipedia.org/wiki/%D0%A1%D0%BB%D0%BE%D0%BD%D0%BE%D0%B2%D1%8B%D0%B5",
            "Кенгуру;2 года;Символ Австралии, передвигается прыжками.;https://ru.wikipedia.org/wiki/%D0%9A%D0%B5%D0%BD%D0%B3%D1%83%D1%80%D1%83",
            "Пингвин;1 год;Птица, которая не умеет летать, но отлично плавает.;https://ru.wikipedia.org/wiki/%D0%9F%D0%B8%D0%BD%D0%B3%D0%B2%D0%B8%D0%BD%D0%BE%D0%B2%D1%8B%D0%B5",
            "Коала;4 года;Ленивый мишка, который всю жизнь ест эвкалипт.;https://ru.wikipedia.org/wiki/%D0%9A%D0%BE%D0%B0%D0%BB%D0%B0",
            "Волк;6 лет;Стайный хищник, санитар леса и герой мемов.;https://ru.wikipedia.org/wiki/%D0%92%D0%BE%D0%BB%D0%BA",
            "Лиса;3 года;Хитрая рыжая плутовка с пушистым хвостом.;https://ru.wikipedia.org/wiki/%D0%9B%D0%B8%D1%81%D0%B8%D1%86%D0%B0"
        )

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "list") {
                composable("list") {
                    ListScreen(animals = animals) { rawData ->
                        val encodedData = URLEncoder.encode(rawData, StandardCharsets.UTF_8.toString())
                        navController.navigate("detail/$encodedData")
                    }
                }
                composable(
                    route = "detail/{animalData}",
                    arguments = listOf(navArgument("animalData") { type = NavType.StringType })
                ) { backStackEntry ->
                    val encodedData = backStackEntry.arguments?.getString("animalData") ?: ""
                    val rawData = URLDecoder.decode(encodedData, StandardCharsets.UTF_8.toString())
                    val parts = rawData.split(";")

                    DetailScreen(
                        name = parts.getOrNull(0) ?: "",
                        age = parts.getOrNull(1) ?: "",
                        desc = parts.getOrNull(2) ?: "",
                        url = parts.getOrNull(3) ?: ""
                    )
                }
            }
        }
    }
}

@Composable
fun ListScreen(animals: List<String>, onAnimalClick: (String) -> Unit) {
    Scaffold(
        topBar = {
            Text(
                text = "Мир Животных (Compose Nav)",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            items(animals) { rawData ->
                val parts = rawData.split(";")
                val name = parts.getOrNull(0) ?: ""
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onAnimalClick(rawData) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailScreen(name: String, age: String, desc: String, url: String) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = name, style = MaterialTheme.typography.headlineLarge)
        Text(text = "Возраст: $age", style = MaterialTheme.typography.titleMedium)
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Text(text = "Описание:", style = MaterialTheme.typography.titleMedium)
        Text(text = desc, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Ссылка на картинку:", style = MaterialTheme.typography.titleSmall)
        Text(
            text = url,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                } catch (e: Exception) {}
            }
        )
    }
}
