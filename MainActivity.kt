package com.example.artspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.artspace.data.DataSource
import com.example.artspace.ui.theme.ArtSpaceTheme

class MainActivity : ComponentActivity() { // Main Activity
    override fun onCreate(savedInstanceState: Bundle?) { //
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController() // Create a NavController
            ArtSpaceTheme {
                NavHost(navController = navController, startDestination = Screen.Home.route + "/{id}") { // Set up the navigation graph

                    // this is the home page where users can see artwork details and navigate between artworks
                    composable(
                        Screen.Home.route + "/{id}", arguments = listOf(navArgument("id") {
                            type = NavType.IntType
                            defaultValue = 0
                        })
                    ) {
                        HomePage(navController = navController)
                    }

                    // this is the artist page where users can learn more about the artists bio and art
                    composable(
                        Screen.Artist.route + "/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) {
                        ArtistPage(navController = navController)
                    }

                }
            }
        }
    }
}
@Composable
fun ArtistPage(navController: NavController) {
    val id = navController.currentBackStackEntry?.arguments?.getInt("id") ?: 0
    val art = DataSource.arts[id]


    // setting up a column to align items vertically, filling the whole screen


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_large)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // this row contains the artist's image and profile information, centered horizontally

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            horizontalArrangement = Arrangement.Center
        ) {
            // adding a frame around the image to create a border
            Box(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.artist_image_size) + dimensionResource(id = R.dimen.padding_small) * 2)
                    .border(
                        BorderStroke(4.dp, Color.Black), // Frame color and width
                        CircleShape //// keeps the border circular, matching the image shape
                    ),
                contentAlignment = Alignment.Center
            ) {
                //  display the artist image
                Image(
                    painter = painterResource(id = art.artistImageId),
                    contentDescription = stringResource(id = art.artistInfoId),
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.artist_image_size))
                        .clip(CircleShape)
                )
            }


              // this column displays the artist's name and birthplace next to their image
            Column(
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_small))
            ) {
                Text(
                    text = stringResource(id = art.artistId),// displays the artist's name
                    style = MaterialTheme.typography.headlineMedium // uses a larger font size for better readability
                )
                Text(
                    text = stringResource(id = art.artistInfoId), // displays the artist's birthplace
                    style = MaterialTheme.typography.titleMedium // uses a smaller font size for better readability
                )
            }
        }

        // artist bio with a scrollable list, as demonstrated in the zoom session

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // allows this section to expand and take up available space
                .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
        ) {
            item {
                Text(
                    text = stringResource(id = art.artistBioId), // displays the artist's bio
                    style = MaterialTheme.typography.bodyLarge // uses a regular size for better readability
                )
            }
        }

        // back button brings the user back to the home page with the same artist ID

        Button(
            onClick = {
                // Navigate back to the HomePage with the same artist ID
                navController.navigate(Screen.Home.route + "/$id") { // Navigate to the home page
                    popUpTo(Screen.Home.route) { inclusive = true } // Clear the back stack
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally) // centers the button horizontally
        ) {
            Text(text = stringResource(id = R.string.back))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Needed for CenterAlignedTopAppBar
@Composable
fun HomePage(navController: NavController) {
    var current by remember { // State to track the current artwork index
        mutableIntStateOf(0) // Starting index for artworks
    }

    val art = DataSource.arts[current] // Get the current artwork based on the index

    Scaffold( // Main Scaffold for the home page
        topBar = { // Top app bar with a title
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding -> // Padding for content
        // Updated Column with verticalArrangement
        Column(
            modifier = Modifier
                .fillMaxSize() // Fill the entire screen
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween // Move buttons to bottom
        ) {
            // Art Display Section
            Column(
                modifier = Modifier.weight(1f) // Takes up the remaining space
            ) {
                ArtWall(
                    artistId = current,
                    artImageId = art.artworkImageId,
                    artDescriptionId = art.descriptionId,
                    navController = navController
                )

                ArtDescriptor(
                    artTitleId = art.titleId,
                    artistId = art.artistId,
                    artYearId = art.yearId
                )
            }

            // "Previous" and "Next" Buttons at the bottom
            DisplayController(current) { newIndex ->
                current = newIndex.coerceIn(0, DataSource.arts.size - 1)
            }
        }
    }
}
@Composable
fun ArtWall(artistId: Int, artImageId: Int, artDescriptionId: Int, navController: NavController) {
    // Box to create a framed artwork display
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_medium)) // Padding for better spacing
            .border(
                BorderStroke(10.dp, Color.Blue), // Frame with a border
                shape = MaterialTheme.shapes.medium // Rounded corners frame
            )
            .padding(8.dp) // Inner padding inside the frame
            .clickable {
                // Navigate to Artist Page
                navController.navigate(Screen.Artist.route + "/$artistId")
            }
    ) {
        Image(
            painter = painterResource(id = artImageId),
            contentDescription = stringResource(id = artDescriptionId),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.0f) // Maintain a square aspect ratio
                .clip(MaterialTheme.shapes.medium) // Rounded corners for the image
        )
    }
}


@Composable
fun ArtDescriptor(artTitleId: Int, artistId: Int, artYearId: Int) { // Artwork title, artist, and year
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.spacer_small)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Artwork title
        Text(
            text = stringResource(id = artTitleId),
            style = MaterialTheme.typography.headlineSmall
        )
        // Artist name and year
        Text(
            text = "${stringResource(id = artistId)} (${stringResource(id = artYearId)})",
            style = MaterialTheme.typography.titleMedium
        )
    }
}




@Composable
fun DisplayController(current: Int, modifier: Modifier = Modifier, updateCurrent: (Int) -> Unit) { // Controller for previous and next buttons
    Row(
        modifier = modifier // Apply the provided modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_medium)), // Padding for alignment
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = { updateCurrent(current - 1) }, // Decrement the current index
            enabled = current > 0 // Disable if there is no previous artwork
        ) {
            Text(text = stringResource(id = R.string.previous))
        }

        Button( // Next button
            onClick = { updateCurrent(current + 1) },
            enabled = current < DataSource.arts.size - 1 // Disable if there is no next artwork
        ) {
            Text(text = stringResource(id = R.string.next))
        }
    }
}