# ListBottomSheet

![Tag](https://img.shields.io/github/v/tag/ZakariaJawas/ListBottomSheet) ![Licence](https://img.shields.io/github/license/ZakariaJawas/ListBottomSheet) ![Downloads](https://img.shields.io/github/downloads/zakariajawas/ListBottomSheet/total)
	
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
implementation 'com.github.ZakariaJawas:ListBottomSheet:{tag}'

```

## Preview

![ListSheet](https://github.com/ZakariaJawas/ListBottomSheet/blob/master/Screenshot%20from%202020-03-15%2018-17-15.png)

## Usage

**_Step 1_**
Create your model

ListBottomSheet adapter requires a list of objects (model) with a field annotated with @NameField 
for exmaple

```kotlin
data class Category(val id: Int, @NameField var name: String)
```
make sure you annotate your title field in the model class with @NameField otherwise **UnspecifiedFieldNameExpection** will be thrown

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

ex: `listSheet.titleSize = 18F`

**_titleColor_**

used to change the color of the title

ex: `listSheet.titleColor = ContextCompat.getColor(this, R.color.colorPrimary)`

## Customization

You can use the following functions with the builder to customize the list sheet

**_itemLayout(Int)_**

used to pass a custom item layout instead of the default layout provided by the library

ex ```kotlin .itemLayout(R.layout.custom_list_item)```

make sure there is a `TextView` with id `textView` so the sheet can display the titles in the list


**_cancelable(Boolean)_**
default value `true`

used to allow or prevent the sheet from being dismissed by clicking outside the sheet

**_cancelButtonVisible(Boolean)_**
default value `false`

used to hide or show the close button in the sheet

## Compatibility
Minimum Android SDK: API level 16

## Author
Zakaria Jawas [@zakariajawas](https://twitter.com/zakariajawas)

## Getting help
If you spot a problem you can open an issue on the Github page, or alternatively, you can contact me via `jawas.zakaria@gmail.com`

Support the library by twitting in ![twitter](https://img.shields.io/twitter/url?url=https%3A%2F%2Fgithub.com%2FZakariaJawas%2FListBottomSheet%2F
)

If you enjoy it, please make this library better :+1:
