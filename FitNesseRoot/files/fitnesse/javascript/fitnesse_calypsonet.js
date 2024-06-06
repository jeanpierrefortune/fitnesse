function adjustHexCells() {
    document.addEventListener("DOMContentLoaded", function () {
        function isHexadecimal(str) {
            return /^[0-9A-Fa-f]+$/.test(str);
        }

        var tds = document.querySelectorAll("table td");

        var index = 0;
        var batchSize = 50;

        function processBatch() {
            for (var i = 0; i < batchSize && index < tds.length; i++, index++) {
                var td = tds[index];
                if (isHexadecimal(td.textContent.trim())) {
                    td.classList.add("hex-cell");
                }
            }
            if (index < tds.length) {
                setTimeout(processBatch, 50);
            }
        }

        processBatch();
    });
}

// Function that will be called when a radio button is selected
function onProfileRadioButtonSelect() {
    alert('Profile Radio button selected');
}

// Function to add a form with radio buttons
function addRadioButtonsToProfilesTOC() {
    // Select the UL list
    const toc1 = document.querySelector('.toc1');

    if (toc1) {
        // Create the form
        const form = document.createElement('form');
        form.id = 'tocForm';

        // Iterate through each LI element and add a radio button
        const listItems = toc1.querySelectorAll('li');
        listItems.forEach((li, index) => {
            // Create the radio button
            const radioButton = document.createElement('input');
            radioButton.type = 'radio';
            radioButton.name = 'tocRadio';
            radioButton.value = index;
            radioButton.id = `tocRadio${index}`;

            // Add an event listener for the radio button
            radioButton.addEventListener('change', onProfileRadioButtonSelect);

            // Create a label for the radio button
            const label = document.createElement('label');
            label.htmlFor = radioButton.id;
            label.appendChild(document.createTextNode(` Select ${li.textContent.trim()}`));

            // Add the radio button and label to the LI element
            li.insertBefore(radioButton, li.firstChild);
            li.insertBefore(label, li.firstChild);
        });

        // Insert the form before the UL list
        toc1.parentNode.insertBefore(form, toc1);
        form.appendChild(toc1);
    }
}

function selectReader() {
    const selectedReader = document.querySelector('input[name="reader"]:checked');
    if (!selectedReader) {
        alert('Please select a reader.');
        return;
    }
    const readerName = selectedReader.value;
    fetch(`/FitNesse.ApplicationSettings.SetReader?responder=test&readerName=` + readerName)
        .then(response => {
            if (response.ok) {
                return response.text(); // Assuming response is HTML
            } else {
                throw new Error('Network response was not ok');
            }
        })
        .then(html => {
            // Store feedback in localStorage and reload the page
            localStorage.setItem('feedback', 'Reader selected successfully!');
            localStorage.setItem('feedbackType', 'feedback-success');
            window.location.reload();
        })
        .catch(error => {
            console.error('Error:', error);
            localStorage.setItem('feedback', 'An error occurred while selecting the reader.');
            localStorage.setItem('feedbackType', 'feedback-error');
            window.location.reload();
        });
}

function showFeedback(type, message) {
    const feedbackContainer = document.getElementById('feedbackContainer');
    feedbackContainer.innerHTML = message;
    feedbackContainer.className = type === 'error' ? 'feedback-container feedback-error' : 'feedback-container feedback-success';
    setTimeout(() => { feedbackContainer.innerHTML = ''; }, 1500); // Hide after 1.5 seconds
}
