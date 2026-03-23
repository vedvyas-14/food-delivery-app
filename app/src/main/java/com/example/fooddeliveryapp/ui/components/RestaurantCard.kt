package com.example.fooddeliveryapp.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fooddeliveryapp.data.model.Restaurant
import com.example.fooddeliveryapp.R


@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    onRestaurantClick: (String) -> Unit
)
{

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                onRestaurantClick(restaurant.id)
            }
    ) {

        Column {
//           val context = LocalContext.current
//
//            val imageLoader = ImageLoader.Builder(context)
//                .okHttpClient(NetworkModule.okHttpClient)
//                .build()

            AsyncImage(
                model = restaurant.imageUrl,
                contentDescription = restaurant.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.image_placeholder),
                error = painterResource(R.drawable.image_placeholder)
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
//                    Text(
//                        text = restaurant.name,
//                        modifier = Modifier.weight(1f),
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                        style = MaterialTheme.typography.titleMedium
//                    )
                    Text(
                        text = restaurant.name,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    RatingBadge(rating = restaurant.rating)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = restaurant.cuisines.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                //Log.d("IMAGE_URL", "https://res.cloudinary.com/swiggy/image/upload/${restaurant.imageId}")
                //Log.d("UI_DEBUG", "Loading: ${restaurant.imageUrl}")
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${restaurant.deliveryTime} mins • ${restaurant.areaName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun RatingBadge(rating: Double) {

    Box(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {

        Text(
            text = "⭐ $rating",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 12.sp
        )
    }
}