import React, { Component } from "react";
import { Admin, List, Datagrid, TextField, EmailField, UrlField, Resource } from 'react-admin';
import { UserList } from './users';
import { RentList } from './rents';
import { DisputeList } from './disputes';
import { ProductList } from './products';
import jsonServerProvider from "ra-data-json-server";
import restProvider from 'ra-data-simple-rest';

//const dataProvider = jsonServerProvider("https://jsonplaceholder.typicode.com");
// const dataProvider = jsonServerProvider("https://dress-app.herokuapp.com");
const dataProvider = restProvider("http://localhost:3000");

class App extends Component {
  render() {
    return (
      <Admin dataProvider={dataProvider}>
        <Resource name="rents/disputes" list={DisputeList} options={{ label: 'Open Disputes' }}/>
        <Resource name="rents/all" list={RentList} options={{ label: 'All Rents' }}/>
        <Resource name="products" list={ProductList} />
        <Resource name="users" list={UserList} />
      </Admin>
    );
  }
}
export default App;