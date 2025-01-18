from item import Item
import errors as err


class ShoppingCart:
    """
        A class to represent a shopping cart.

        Attributes:
        ----------
        items : list
            A list to store items in the shopping cart.

        Methods:
        -------
        add_item(item: Item):

        remove_item(item_name: str):

        get_subtotal() -> int:
    """

    def __init__(self):
        self.items = []

    def add_item(self, item: Item):
        """
        Adds an item to the shopping cart.

            Parameters:
            ----------
            item : Item
                The item to be added to the cart.

            Raises:
            ------
            ItemAlreadyExistsError:
                If the item is already in the shopping cart.
        """
        if item in self.items:
            raise err.ItemAlreadyExistsError(f"Item '{item.name}' is already in the shopping cart!")
        self.items.append(item)

    def remove_item(self, item_name: str):
        """
        Removes an item from the shopping cart by its name.

            Parameters:
            ----------
            item_name : str
                The name of the item to be removed from the cart.

            Raises:
            ------
            ItemNotExistError:
                If the item is not found in the shopping cart.
        """
        if not any(item.name == item_name for item in self.items):
            raise err.ItemNotExistError(f"Item '{item_name}' is not in the shopping cart!")
        for item in self.items:
            if item.name == item_name:
                self.items.remove(item)

    def get_subtotal(self) -> int:
        """
        Calculates the total price of all items in the shopping cart.

            Returns:
            -------
            int
            The total price of all items in the shopping cart.
        """
        return sum(item.price for item in self.items)
