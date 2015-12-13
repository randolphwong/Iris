#include "Cluster.h"

void merge(uint16_t a[], const uint8_t low, const uint8_t mid, const uint8_t high)
{
	uint16_t *temp = new uint16_t[high-low+1];
        
	uint8_t left = low;
	uint8_t right = mid+1;
	uint8_t current = 0;
	// Merges the two arrays into temp[] 
	while(left <= mid && right <= high) {
		if(a[left] <= a[right]) {
			temp[current] = a[left];
			left++;
		}
		else { // if right element is smaller that the left
			temp[current] = a[right];  
			right++;
		}
		current++;
	}

	// Completes the array 

        // Extreme example a = 1, 2, 3 || 4, 5, 6
        // The temp array has already been filled with 1, 2, 3, 
        // So, the right side of array a will be used to fill temp.
	if(left > mid) { 
		for(int i=right; i <= high;i++) {
			temp[current] = a[i];
			current++;
		}
	}
        // Extreme example a = 6, 5, 4 || 3, 2, 1
        // The temp array has already been filled with 1, 2, 3
        // So, the left side of array a will be used to fill temp.
	else {  
		for(int i=left; i <= mid; i++) {
			temp[current] = a[i];
			current++;
		}
	}
	// into the original array
	for(int i=0; i<=high-low;i++) {
                a[i+low] = temp[i];
	}
	delete[] temp;
}

void merge_sort(uint16_t a[], const uint8_t low, const uint8_t high)
{
	if(low >= high) return;
	uint8_t mid = (low+high)/2;
	merge_sort(a, low, mid);  //left half
	merge_sort(a, mid+1, high);  //right half
	merge(a, low, mid, high);  //merge them
}

void adjacent_difference(uint16_t a[], uint8_t size, uint16_t dest[]) {
    dest[0] = a[0];
    for (int i = 1; i != size; ++i)
        dest[i] = a[i] - a[i - 1];
}

void printArray(uint16_t arr[], uint8_t size) {
    for (int i = 0; i != size; ++i) {
        Serial.print(arr[i]);
        Serial.print(" ");
    }
}

Cluster::Cluster() {
    size = 0;
    pos = 0;
    cluster_count = 0;
}

uint8_t Cluster::getSize() {
    return size;
}

void Cluster::print() {
    Serial.println("Current overall time database:");
    printArray(overallArray, size);
    Serial.println();

    //std::cout << "cluster," << " count is: " << cluster_count << std::endl;
    Serial.println("Current clusters:");
    if (cluster_count > 0) {
        for (int i = 0; i != cluster_count; ++i) {
            Serial.print("cluster #");
            Serial.print(i);
            Serial.print(": ");
            for (int j = 0; j != cluster_size[i]; ++j) {
                Serial.print(clusteredArray[i][j]);
                Serial.print(" ");
            }
            Serial.println();
        }
    }
}

void Cluster::add(uint16_t instance) {
    overallArray[pos] = instance;
    pos = (pos + 1) % MAX_SIZE;
    size = (size == MAX_SIZE) ? size : size + 1;
    buildCluster();
}

bool Cluster::classify() {
    if (size == 0) return false;
    int8_t classification = _classify(overallArray[pos - 1]);
    if (classification == -1) return false;

    int minimumFrequency = size / cluster_count;
    return (cluster_size[classification] >= minimumFrequency);
}

int8_t Cluster::_classify(uint16_t instance) {
    for (int i = 0; i != cluster_count; ++i) {
        for (int j = 0; j != cluster_size[i]; ++j) {
            if (clusteredArray[i][j] == instance) return i;
        }
    }
    return -1;
}

void Cluster::buildCluster() {

    // reset cluster count
    cluster_count = -1;

    // reset cluster sizes
    for (int i = 0; i != MAX_CLUSTER_COUNT; ++i)
        cluster_size[i] = 0;

    uint16_t overallCopy[size];

    for (int i = 0; i != size; ++i)
        overallCopy[i] = overallArray[i];

    merge_sort(overallCopy, 0, size - 1);
    
    uint16_t diff[size];
    adjacent_difference(overallCopy, size, diff);



    int j = 0;
    for (int i = 0; i < size; ++i) {
        if ((diff[i] > 100) || i == 0) {
            cluster_count++;
            j = 0;
        }

        clusteredArray[cluster_count][j++] = overallCopy[i];
        cluster_size[cluster_count]++;
 
    }
    cluster_count++;
}
