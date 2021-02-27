# ListBottomSheet

![Tag](https://img.shields.io/github/v/tag/ZakariaJawas/ListBottomSheet) [![jitpack](https://jitpack.io/v/ZakariaJawas/ListBottomSheet.svg)](https://jitpack.io/#ZakariaJawas/ListBottomSheet) ![Licence](https://img.shields.io/github/license/ZakariaJawas/ListBottomSheet) ![Stars](https://img.shields.io/github/stars/ZakariaJawas/ListBottomSheet)
	
## Next Release v1.3.0
- [ ] Multiple selection list

## Release v1.2.3
- [x] Searchable list
- [x] Selected item indicator
- [x] Adding action button


## Installation
Add Jitpack to your repositories in your project level `build.gradle` file

```
allprojects {
    repositories {
      // ...
      maven { url 'https://jitpack.io' }
    }
}
```

Add the below to your dependencies, in your `build.gradle` file


```
implementation 'com.github.ZakariaJawas:ListBottomSheet:Tag'
```

## Preview
_Basic_

<img src="https://github.com/ZakariaJawas/ListBottomSheet/blob/master/Screenshot_1586762142.png" height="600">

_Searchable_

<img src="https://github.com/ZakariaJawas/ListBottomSheet/blob/master/Screenshot_1586760454.png" height="600">

_Selected Item_

<img src="https://github.com/ZakariaJawas/ListBottomSheet/blob/master/Screenshot_1586760585.png" height="600">

## Usage

**_Step 1_**
Create your model

ListBottomSheet adapter requires a list of objects (model) with a field annotated with @NameField 
for exmaple

```kotlin
data class Category(val id: Int, @NameField var name: String)
```
make sure to annotate your title field in your model class with @NameField otherwise **UnspecifiedFieldNameExpection** will be thrown

**_Step 2_**
Build the sheet

```kotlin
 val mList = listOf(Category(1, "Category 1"), Category(2, "Category 2"), Category(3, "Category 3"))
 val listSheet = ListBottomSheet.Builder<Category>(this)
            .list(mList)
            .title("Choose one")
            .onChooseItemCallback { sheet: ListBottomSheet<Category>, category: Category, position: Int ->
                sheet.dismiss() //hide the dialog
            }            
            .build()        
```

**_Step 3_**
Show the list sheet

```kotlin
listSheet.show()
```

## Available Properities

**_titleAlignment_**

used to change the alignment of the title

| Values         |
| -------------- |
| Gravity.RIGHT  |
| Gravity.LEFT   |
| Gravity.CENTER |

**_titleSize_**

used to change the size of the title

```kotlin
listSheet.titleSize = 18F
```

**_titleColor_**

used to change the color of the title

```kotlin
listSheet.titleColor = ContextCompat.getColor(this, R.color.colorPrimary)
```

**_selectedItemIndex_**

used to change the selected item in the list at any time in the code

```kotlin
listSheet.selectedItemIndex = 2
```

## Customization

You can use the following functions with the builder to customize the list sheet

**_itemLayout(Int)_**

used to pass a custom item layout instead of the default layout provided by the library

```kotlin
.itemLayout(R.layout.custom_list_item)
```

make sure there is a `TextView` with id `textView` so the sheet can display the titles in the list


**_cancelable(Boolean)_**
default value `true`

used to allow or prevent the sheet from being dismissed by clicking outside the sheet

**_cancelButtonVisible(Boolean)_**
default value `false`

used to hide or show the close button in the sheet

**_selectedItemColor(Int)_**

param Int is **color integer** value not **color resource id**

used to change the deafult selected item color, default value (BLACK)

```kotlin
.selectedItemColor(ContextCompat.getColor(this, R.color.colorAccent))
```

**_selectedItemIndex(Int)_**

used to build the sheet with initial selected item in the list, default value `-1` means no selected item

```kotlin
.selectedItemIndex(2)
```

**_searchable(Boolean)_**
default value `false`

used to enable the search functionality in the bottom sheet

**setActionButtonTitle(String)_**

used to change the action button title, default value `Ok`

**setOnActionCallback(ListBottomSheet)_**

this is a callback for the action button click listener, it will return an instance of the current sheet so it can be dismissed

```kotlin
.setOnActionCallback {
                Toast.makeText(this, "On Action Button Clicked", Toast.LENGTH_SHORT).show()
                it.dismiss()
            }
```

## Compatibility
Minimum Android SDK: API level 16

## Author
Zakaria Jawas [@zakariajawas](https://twitter.com/zakariajawas)

## Getting help
If you spot a problem you can open an issue on the Github page, or alternatively, you can contact me via `jawas.zakaria@gmail.com`

Support the library by twitting in ![twitter](https://img.shields.io/twitter/url?url=https%3A%2F%2Fgithub.com%2FZakariaJawas%2FListBottomSheet%2F
)

If you enjoy it, please make this library better :+1:
